package RSSFeed;

import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;
import junit.framework.TestCase;

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
}
