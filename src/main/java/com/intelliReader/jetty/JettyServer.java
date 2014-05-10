package com.intelliReader.jetty;

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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/27/14
 * Time: 11:02 PM
 */


public class JettyServer extends ServletContextHandler {
    public KeywordBasedFeedRelevanceModel getModel() {
        return model;
    }

    private KeywordBasedFeedRelevanceModel model;

    public JettyServer() throws Exception {
        initModel();
    }

    private void initModel() throws Exception {
        String projRoot = System.getProperty("user.dir");
        String dbPath = projRoot + "/src/main/resources/iReader";
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
        model.addFeed(new Feed(title, null, null, null, null, null), Calendar.getInstance().getTime());
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
        JettyServer ajaxHandler = new JettyServer();
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
            List<Pair> lst = new ArrayList<Pair>();
            for(String k: wordScores.getKeys())
            {
                lst.add(new Pair(k,wordScores.get(k)));
            }
            Collections.sort(lst);

            for(Pair p: lst)
            {
                response.getWriter().println("<p>" + p.k + "|" + wordScores.get(p.k) + "|" + wordDates.get(p.k) + "</p>" );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().println("</body>\n" + "</html>");
    }
}

class Pair implements Comparable<Pair>{

    double s;
    String k;
    public Pair(String str,double d)
    {
        s = d;
        k = str;
    }

    @Override
    public int compareTo(Pair pair) {
        if(this.s < pair.s)
            return 1;
        if(this.s == pair.s)
            return 0;
        return -1;
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
            RSSFeedParser parser = new RSSFeedParser(rss);
            Feed feed = parser.readFeed();
            List<FeedMessage> messages = feed.getMessages();
            response.getWriter().println("<p class=\"heading\">" + RSSSources.feeds.get(rss) + "</p>");
            response.getWriter().println("<div class=\"content\">");
            for(FeedMessage message:messages)
            {
                response.getWriter().println("<p><a onclick=\"sendText(this)\" href=\"" +
                        message.getLink() + "\">"+message.getTitle()+"</a>" +
                        "<small>" + message.getDescription() +"</small></p>" );
            }
            response.getWriter().println("</div>");
        }
        response.getWriter().println("</div></body>\n" + "</html>");
    }

    String htmlPageHeader = "<!DOCTYPE html><html>\n" +
            "<head>\n" +
            "    <meta charset=\"utf-8\"/>\n" +
            "    <title>iReader</title>\n" +
            "    <script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js\"></script>\n" +
            "    <script type=\"text/javascript\">\n" +
            "       jQuery(document).ready(function() {\n" +
            "           jQuery(\".content\").hide();\n" +
            "           //toggle the componenet with class msg_body\n" +
            "           jQuery(\".heading\").click(function()\n" +
            "           {\n" +
            "               jQuery(this).next(\".content\").slideToggle(500);\n" +
            "           });\n" +
            "       }); \n" +
            "    </script> " +
            "    <script type=\"text/javascript\">\n" +
            "        var req;\n" +
            "        function sendText(id) {\n" +
            "            var title=id.firstChild.data;\n" +
            "            if (typeof XMLHttpRequest != \"undefined\") {\n" +
            "                req = new XMLHttpRequest();\n" +
            "            } else if (window.ActiveXObject) {\n" +
            "                req = new ActiveXObject(\"Microsoft.XMLHTTP\");\n" +
            "            }\n" +
            "            var ip = location.host; \n" +
            "            var base = \"http://\" + ip + \"/randomBase?t=\" + Math.random() + \"&id=\";\n" +
            "            var url = base.concat(title);\n" +
            "            req.open(\"GET\",url,true);\n" +
            "            req.send(null);\n" +
            "        }\n" +
            "\n" +
            "    </script>\n" +
            "</head>\n" +
            "<body>" +
            "<div style=\"column-count:4;\">";

}