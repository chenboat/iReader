package RSSFeed;

import com.intelliReader.newsfeed.AeonRSSFeedDescriptor;
import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;
import junit.framework.TestCase;

/**
 * User: ting
 * Date: 12/27/2017
 * Time: 2:58 PM
 */
public class FeedMessageTest extends TestCase {
    public void testFeedMessageStripImageUrl() {
        FeedMessage fm = new FeedMessage();
        fm.setDescription("Some description: " +
                "<img src=\"https://nu.aeon.co/images/c1620b95-b107-485b-b934-91a5cbf5a63e/" +
                "show_sized-steinlen-2319102115580098.jpg\" alt=\"\">");
        assertEquals("Some description: ", fm.getDescription());
    }
}
