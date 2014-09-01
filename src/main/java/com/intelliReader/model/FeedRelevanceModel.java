package com.intelliReader.model;

import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.model.KeywordBasedFeedRelevanceModel.ScoredFeedMessage;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/14/14
 * Time: 10:26 PM
 */
public interface FeedRelevanceModel {
    // Given a list of feeds, order the feeds based on their relevance
    public List<ScoredFeedMessage> rankFeeds(List<FeedMessage> inputList, Date date);

    // Add a viewed feed with the view date to the model
    public void addFeed(FeedMessage f, Date viewDate);

    // Initialize the model
    public void initModel();

}
