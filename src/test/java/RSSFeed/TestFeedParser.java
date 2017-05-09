package RSSFeed;

import com.intelliReader.newsfeed.AeonRSSFeedDescriptor;
import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;
import junit.framework.TestCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: ting
 * Date: 10/31/2016
 * Time: 11:21 PM
 */
public class TestFeedParser extends TestCase {
    public void testHypothesis() {
        try {
            RSSFeedParser parser = new RSSFeedParser("http://www.nytimes.com/services/xml/rss/nyt/Environment.xml");
            Feed feed = parser.readFeed();
            for (FeedMessage message : feed.getMessages()) {
                System.out.println(message.getTitle());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void testAeonRSSFeed() {
        AeonRSSFeedDescriptor descriptor = new AeonRSSFeedDescriptor("aeon");
        try {
            RSSFeedParser parser = new RSSFeedParser("https://aeon.co/feed.rss");
            Feed feed = parser.readFeed();
            for (FeedMessage message : feed.getMessages()) {
                    System.out.println(message.getTitle() + " " + descriptor.getPictureUrl(message));
                System.out.println("===============================");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
