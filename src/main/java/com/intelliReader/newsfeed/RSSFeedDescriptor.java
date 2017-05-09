package com.intelliReader.newsfeed;

/**
 * An interface for extracting info from a RSSFeed (e.g., summary, section and so on).
 * Date: 5/7/2017
 * Time: 10:19 PM
 */
public interface RSSFeedDescriptor {
    // Returns the category of the RSSFeed.
    public String getCategory();
    // Return the pic url of the RSSFeed.
    public String getPictureUrl(FeedMessage feedMessage);
}
