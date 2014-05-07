package RSSFeed;

import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/13/14
 * Time: 11:04 PM
 */
public class FeedTest {
    public static void main(String[] args) {
        RSSFeedParser parser = new RSSFeedParser("http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml");
        Feed feed = parser.readFeed();
        System.out.println(feed);
        for (FeedMessage message : feed.getMessages()) {
            System.out.println(message);
        }

    }
}
