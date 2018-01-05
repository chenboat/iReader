package com.intelliReader.newsfeed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: ting
 * Date: 5/7/2017
 * Time: 10:30 PM
 */
public class AeonRSSFeedDescriptor implements RSSFeedDescriptor {
    private static final Pattern p = Pattern.compile(
            "https://[^\"]*.jpg"); // the regex to match the picture url
    private String category;
    public AeonRSSFeedDescriptor(String cat) {
        this.category = cat;
    }
    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getPictureUrl(FeedMessage feedMessage) {
        Matcher m = p.matcher(feedMessage.getOriginalDescription());
        if (m.find()) {
            return m.group();
        }
        return null;
    }
}
