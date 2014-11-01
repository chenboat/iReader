package jetty;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/27/14
 * Time: 11:02 PM
 */
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.model.Stemmer;
import com.intelliReader.model.StopWordFilter;
import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;
import com.intelliReader.newsfeed.RSSSources;
import com.intelliReader.storage.BerkelyDBStore;
import com.intelliReader.storage.Store;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class JettyAjax extends ServletContextHandler {
    public KeywordBasedFeedRelevanceModel getModel() {
        return model;
    }

    private KeywordBasedFeedRelevanceModel model;

    public JettyAjax() throws Exception {
        initModel();
    }

    private void initModel() throws Exception {
        String projRoot = System.getProperty("user.dir");
        String dbPath = projRoot + "/src/test/resources/iReader";
        System.out.println("DBPath:" + dbPath);
        BerkelyDBStore<String, Double> wordScoresStore = new BerkelyDBStore<String, Double>(dbPath, String.class, Double.class, "scoreTable" );
        BerkelyDBStore<String, Date> wordLastUpdatedDatesStore = new BerkelyDBStore<String, Date>(dbPath, String.class, Date.class, "dateTable");
        System.out.println("Word store size:" + wordScoresStore.getKeys().size());
        model = new KeywordBasedFeedRelevanceModel(
                wordScoresStore,
                wordLastUpdatedDatesStore,
                new StopWordFilter(),
                new Stemmer());
    }

    public void doHandle(String target, Request baseRequest, HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
        System.out.println(request.getRequestURL());
        String title = request.getParameter("id");
        System.out.println(title);
        model.addFeed(new FeedMessage(title, null), Calendar.getInstance().getTime());
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        response.setContentLength(19 + title.length());
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("<message>" + title + "</message>");
        response.flushBuffer();
        baseRequest.setHandled(true);
    }

    public static void main(String[] args) throws Exception {
        //TODO: reduce the number of handlers
        Server server = new Server(8081);
        JettyAjax ajaxHandler = new JettyAjax();
        ajaxHandler.setContextPath("/randomBase");

        CountingPage cp = new CountingPage(ajaxHandler.getModel());
        cp.setContextPath("/count");

        FrontPage frontPage = new FrontPage();
        frontPage.setContextPath("/");

        ContextHandlerCollection handlers = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[] { frontPage, ajaxHandler,cp });

        server.setHandler(handlers);
        server.start();
        server.join();
    }
}

class CountingPage extends ServletContextHandler {
    private final KeywordBasedFeedRelevanceModel model;
    public CountingPage(KeywordBasedFeedRelevanceModel m)
    {
        model = m;
    }

    @Override
    public void doHandle(String target,
                         Request baseRequest,
                         HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<html>\n <body>");
        Store<String,Double> wordScores = model.getWordScores();
        Store<String,Date> wordDates = model.getWordLastUpdatedDates();
        try {
            for(String k: wordScores.getKeys())
            {
                response.getWriter().println("<p>" + k + "|" + wordScores.get(k) + "|" + wordDates.get(k) + "</p>" );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().println("</body>\n" + "</html>");
    }
}

class FrontPage extends ServletContextHandler {
    @Override
    public void doHandle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println(htmlPageHeader);

        for(String rss: RSSSources.feeds.keySet())
        {
            try{
                RSSFeedParser parser = new RSSFeedParser(rss);
                Feed feed = parser.readFeed();
                List<FeedMessage> messages = feed.getMessages();
                response.getWriter().println("<h4>" + RSSSources.feeds.get(rss) + "</h4>");
                for(FeedMessage message:messages)
                {
                    response.getWriter().println("<p><a onclick=\"sendText(this)\" href=\"" +
                            message.getLink() + "\">"+message.getTitle()+"</a></p>" );
                }
            }catch (XMLStreamException e)
            {
            }
        }
        response.getWriter().println("</div></body>\n" + "</html>");
    }

    String htmlPageHeader = "<!DOCTYPE html><html>\n" +
            "<head>\n" +
            "    <meta charset=\"utf-8\"/>\n" +
            "    <title>AJAX Test</title>\n" +
            "    <script type=\"text/javascript\">\n" +
            "        var req;\n" +
            "        function sendText(id) {\n" +
            "            var title=id.firstChild.data;\n" +
            "\n" +
            "            if (typeof XMLHttpRequest != \"undefined\") {\n" +
            "                req = new XMLHttpRequest();\n" +
            "            } else if (window.ActiveXObject) {\n" +
            "                req = new ActiveXObject(\"Microsoft.XMLHTTP\");\n" +
            "            }\n" +
            "            var base = \"http://192.168.194.130:8081/randomBase?t=\" + Math.random() + \"&id=\";\n" +
            "            var url = base.concat(title);\n" +
            "            req.open(\"GET\",url,true);\n" +
            "            req.send(null);\n" +
            "        }\n" +
            "\n" +
            "    </script>\n" +
            "</head>\n" +
            "<body>" +
            "<div style=\"column-count:3;\">";

}