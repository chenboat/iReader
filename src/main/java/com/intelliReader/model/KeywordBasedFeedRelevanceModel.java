package com.intelliReader.model;

import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.storage.MongoDBStore;
import com.intelliReader.storage.Store;
import com.intelliReader.text.TextAnalyzer;
import com.intelliReader.util.StringUtil;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/14/14
 * Time: 10:27 PM
 *
 * This is a ranking model based on keyword and temporal information of a reader's reading history
 * Specificially, the model contains
 * 1. For each keyword w in a feed f viewed by the user
 *       s(w) = sum(0.5 ^ d_i) where d_i == (today's date - the date that f is clicked or viewed)
 *    Note that if a feed is viewed multiple times, it will make several contributions of the keywords it contains
 * 2. For each feed f to rank, its score
 *       rs(f) = sum(s(w)) where s(w) is the score of word w appearing in f
 * 3. For each f we will remove all its stop-word (e.g., the, a, and) and perform a word normalization (e.g.,
 *               working == work, studying == study, financially == financial)
 */
public class KeywordBasedFeedRelevanceModel implements FeedRelevanceModel {
    Store<String,Double> wordScores;
    Store<String,Date> wordLastUpdatedDates;
    StopWordFilter stopWordFilter; // the word in the filter is NOT prefixed by user id
    Stemmer stemmer;
    String id; // the user id which is be the prefix of the keys of relevant records of the user in each table
    Logger log = Logger.getLogger(KeywordBasedFeedRelevanceModel.class.getName());

    public KeywordBasedFeedRelevanceModel(Store<String, Double> scoreTable,
                                          Store<String, Date> dateTable,
                                          StopWordFilter stopWordFilter,
                                          Stemmer stemmer,
                                          String id) {
        wordScores = scoreTable;
        wordLastUpdatedDates = dateTable;
        this.stopWordFilter = stopWordFilter;
        this.stemmer = stemmer;
        this.id = id;
    }

    public Store<String, Double> getWordScores() {
        return wordScores;
    }

    public Store<String, Date> getWordLastUpdatedDates() {
        return wordLastUpdatedDates;
    }

    public KeywordBasedFeedRelevanceModel(Store<String,Double> wordScoresStore,
                                          Store<String,Date> wordLastUpdatedDatesStore,
                                          StopWordFilter filter,
                                          Stemmer s)
    {
        this(wordScoresStore,wordLastUpdatedDatesStore,filter,s,null);
    }

    public void initModel()
    {
        // Load the model to wordScores and wordLastUpdatedDates

    }

    @Override
    public List<ScoredFeedMessage> rankFeeds(List<FeedMessage> inputList, Date date) {
        List<ScoredFeedMessage> lst = new ArrayList<ScoredFeedMessage>();
        Map<String,Double> wordScoresCache = null;
        Map<String,Date> wordLastUpdatedDatesCache = null;
        // caching all the key value map from the underlying stores for faster retrieval
        try {
            wordScoresCache = wordScores.getAll();
            wordLastUpdatedDatesCache = wordLastUpdatedDates.getAll();
        }catch (Exception e){
            log.log(Level.SEVERE, "Getting caches from the word stores failed: " + e.toString());
        }
        for(FeedMessage f: inputList)
        {
            double score = 0;
            String desc = f.getDescription() + " " + f.getTitle();
            Map<String, Double> wordsWithScores = new HashMap<String, Double>();
            for(String w : TextAnalyzer.tokenizeLowerCaseAndRemoveStopWordAndStem(desc.trim(),stopWordFilter,stemmer))
            {
                String key = StringUtil.makeSSTableKey(id,w);   // use the user id + word composite key
                try{
                    if(wordScoresCache.get(key) != null)
                    {
                        assert  wordLastUpdatedDatesCache.get(key) != null;
                        // Taking into consideration of time lapses
                        double wScore = ModelUtil.exponentialDecayScore(wordScoresCache.get(key),
                                                                        wordLastUpdatedDatesCache.get(key),date);
                        score += wScore;
                        wordsWithScores.put(key,wScore);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            lst.add(new ScoredFeedMessage(score,f,wordsWithScores));
        }

        Collections.sort(lst,Collections.reverseOrder());

        return lst;
    }

    @Override
    public void addFeed(FeedMessage f, Date viewDate) {
        String desc = f.getDescription() + " " + f.getTitle();

        for(String w : TextAnalyzer.tokenizeLowerCaseAndRemoveStopWordAndStem(desc.trim(),stopWordFilter,stemmer))
        {
            String key = StringUtil.makeSSTableKey(id,w);   // use the user id + word composite key
            try{
                if(wordScores.get(key) != null)
                {
                    assert wordLastUpdatedDates.get(key) != null; // both map should have the key
                    Date prevDate = wordLastUpdatedDates.get(key);
                    assert viewDate.after(prevDate) || viewDate.equals(prevDate);

                    double score = wordScores.get(key);
                    score = ModelUtil.exponentialDecayScore(score,prevDate,viewDate) + 1.0;

                    wordScores.put(key,score);
                    wordLastUpdatedDates.put(key,viewDate);
                }
                else
                {
                    wordScores.put(key,1.0);
                    wordLastUpdatedDates.put(key,viewDate);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        wordScores.sync();
        wordLastUpdatedDates.sync();
    }

    public class ScoredFeedMessage implements Comparable<ScoredFeedMessage>
    {
        FeedMessage msg;
        double score;
        Map<String, Double> wordWithScores; // a set of words in the message with scores: all chars are in lower-case

        public Map<String, Double> getWordWithScores() {
            return wordWithScores;
        }

        public void setWordWithScores(Map<String, Double> wordWithScores) {
            this.wordWithScores = wordWithScores;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public FeedMessage getMsg() {
            return msg;
        }

        public void setMsg(FeedMessage msg) {
            this.msg = msg;
        }

        ScoredFeedMessage(double s, FeedMessage m, Map<String, Double> words)
        {
            score = s;
            msg = m;
            wordWithScores = words;
        }

        @Override
        public int compareTo(ScoredFeedMessage scoredFeed) {
            if(this.score < scoredFeed.score)
                return -1;
            else if(this.score == scoredFeed.score)
                return 0;
            else
                return 1;
        }
    }

}
