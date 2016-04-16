package RSSFeed;

import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;

import javax.management.modelmbean.XMLParseException;
import javax.xml.stream.XMLStreamException;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/13/14
 * Time: 11:04 PM
 */
public class FeedTest {
    public static void main(String[] args) throws XMLStreamException {
        RSSFeedParser parser = new RSSFeedParser("http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml");
        Feed feed = parser.readFeed();
        for (FeedMessage message : feed.getMessages()) {
            assert !message.getLink().isEmpty();
            assert !message.getTitle().isEmpty();
        }

    }
}
