package com.intelliReader.model;

import com.intelliReader.newsfeed.Feed;
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

    @Override
    public List<Feed> rankFeeds(List<Feed> inputList, Date date) {
        List<ScoredFeed> lst = new ArrayList<ScoredFeed>();
        for(Feed f: inputList)
        {
            double score = 0;
            String desc = f.getDescription() + " " + f.getTitle();
            for(String word : desc.trim().split(" "))
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
                        score += wordScores.get(w) * Math.pow(0.5,timeLapseInDays(wordLastUpdatedDates.get(w),date));
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            lst.add(new ScoredFeed(score,f));
        }

        Collections.sort(lst);
        List<Feed> sortedFeedLst = new ArrayList<Feed>();
        for(ScoredFeed sf: lst)
        {
            sortedFeedLst.add(sf.feed);
        }
        return sortedFeedLst;
    }

    @Override
    public void addFeed(Feed f, Date viewDate) {
        String desc = f.getDescription() + " " + f.getTitle();

        for(String word : desc.trim().split(" "))
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
                    score = score * Math.pow(0.5,timeLapseInDays(prevDate,viewDate)) + 1.0;

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

    private int timeLapseInDays(Date prev, Date now)
    {
        assert now.after(prev) || prev.equals(now);
        long timeLapseInMiscSec = now.getTime() - prev.getTime();
        return (int) (timeLapseInMiscSec / (1000 * 3600 * 24));

    }

    private class ScoredFeed implements Comparable<ScoredFeed>
    {
        double score;
        Feed feed;

        ScoredFeed(double s, Feed f)
        {
            score = s;
            feed = f;
        }
        @Override
        public int compareTo(ScoredFeed scoredFeed) {
            if(this.score < scoredFeed.score)
                return -1;
            else if(this.score == scoredFeed.score)
                return 0;
            else
                return 1;
        }
    }

}