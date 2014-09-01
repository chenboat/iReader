package jetty;

import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;
import junit.framework.TestCase;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/26/14
 * Time: 4:44 PM
 */
public class HelloWorld extends AbstractHandler {
    private static List<FeedMessage> messages;
    static {
        RSSFeedParser parser = new RSSFeedParser("http://feeds.reuters.com/reuters/technologyNews");
        Feed feed = parser.readFeed();
        messages = feed.getMessages();
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>Latest News:</h1>");
        for(FeedMessage message:messages)
        {
            response.getWriter().println("<p><a href=\"" + message.getLink() + "\">"+message.getTitle()+"</a></p>" );
        }
    }

    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new HelloWorld());

        server.start();
        server.join();
    }



}
