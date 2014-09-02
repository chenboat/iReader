package com.intelliReader.model;

import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.storage.Store;

import java.util.*;

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
    StopWordFilter stopWordFilter;
    Stemmer stemmer;
    static String[] EMPTY_STRING = new String[0];

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
        wordScores = wordScoresStore;
        wordLastUpdatedDates = wordLastUpdatedDatesStore;
        stopWordFilter = filter;
        stemmer = s;
    }

    public void initModel()
    {
        // Load the model to wordScores and wordLastUpdatedDates

    }

    private String[] tokenize(String s) {
        if(s == null)
        {
            return EMPTY_STRING;
        }
        else
        {
            return s.split("\\W");
        }

    }
    @Override
    public List<ScoredFeedMessage> rankFeeds(List<FeedMessage> inputList, Date date) {
        List<ScoredFeedMessage> lst = new ArrayList<ScoredFeedMessage>();
        for(FeedMessage f: inputList)
        {
            double score = 0;
            String desc = f.getDescription() + " " + f.getTitle();
            for(String word : tokenize(desc.trim()))
            {
                word = word.toLowerCase();
                if(stopWordFilter.isStopWord(word))
                {
                    continue;
                }

                String w = stem(word);
                try{
                    if(wordScores.get(w) != null)
                    {
                        assert  wordLastUpdatedDates.get(w) != null;
                        // Taking into consideration of time lapses
                        score += ModelUtil.exponentialDecayScore(wordScores.get(w),wordLastUpdatedDates.get(w),date);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            lst.add(new ScoredFeedMessage(score,f));
        }

        Collections.sort(lst,Collections.reverseOrder());

        return lst;
    }

    @Override
    public void addFeed(FeedMessage f, Date viewDate) {
        String desc = f.getDescription() + " " + f.getTitle();

        for(String word : tokenize(desc.trim()))
        {
            word = word.toLowerCase();
            if(stopWordFilter.isStopWord(word)) continue; // a stop word will not be counted
            String w = stem(word);
            try{
                if(wordScores.get(w) != null)
                {
                    assert wordLastUpdatedDates.get(w) != null; // both map should have the key
                    Date prevDate = wordLastUpdatedDates.get(w);
                    assert viewDate.after(prevDate) || viewDate.equals(prevDate);

                    double score = wordScores.get(w);
                    score = ModelUtil.exponentialDecayScore(score,prevDate,viewDate) + 1.0;

                    wordScores.put(w,score);
                    wordLastUpdatedDates.put(w,viewDate);
                }
                else
                {
                    wordScores.put(w,1.0);
                    wordLastUpdatedDates.put(w,viewDate);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        wordScores.sync();
        wordLastUpdatedDates.sync();
    }

    private String stem(String word) {
        stemmer.add(word.toCharArray(),word.length());
        stemmer.stem();
        return stemmer.toString();
    }

    public class ScoredFeedMessage implements Comparable<ScoredFeedMessage>
    {
        FeedMessage msg;
        double score;

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

        ScoredFeedMessage(double s, FeedMessage m)
        {
            score = s;
            msg = m;
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
