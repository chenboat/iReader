package com.intelliReader.jetty;

import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.model.Stemmer;
import com.intelliReader.model.StopWordFilter;
import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;
import com.intelliReader.newsfeed.RSSSources;
import com.intelliReader.storage.BerkelyDBStore;
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
    private KeywordBasedFeedRelevanceModel model;
    private String rankListHTML; // this is the HTML which lists all feed messages in reverse scores
    private String sectionHTML; // this is the HTML which lists each feed with its message in a separate section

    public KeywordBasedFeedRelevanceModel getModel() {
        return model;
    }

    public String getRankListHTML() {
        return rankListHTML;
    }

    public String getSectionHTML() {
        return sectionHTML;
    }

    public JettyServer() throws Exception {
        initModel();
    }

    private void initModel() throws Exception {
        String projRoot = System.getProperty("user.dir");
        // First load the word and score info stored in bdb
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

        // Next fetch the latest feed messages and build the html tags
        StringBuffer sb = new StringBuffer();
        for(String rss: RSSSources.feeds.keySet())
        {
            RSSFeedParser parser = new RSSFeedParser(rss);
            Feed feed = parser.readFeed();
            List<FeedMessage> messages = feed.getMessages();
            sb = sb.append("<p class=\"heading\">" + RSSSources.feeds.get(rss) + "</p>\n");
            sb = sb.append("<div class=\"content\">\n");
            for(FeedMessage message:messages)
            {
                sb = sb.append("<p><a onclick=\"sendText(this)\" href=\"" +
                        message.getLink() + "\">"+message.getTitle()+"</a>" +
                        "<small>" + message.getDescription() +"</small></p>\n" );
            }
            sb = sb.append("</div>\n");
        }
        sectionHTML = sb.toString();

        sb = new StringBuffer();

        // Compute the scores for each article and sort the list the scores
        Set<String> msgHash = new HashSet<String>();
        List<FeedMessage> feedMsgs = new ArrayList<FeedMessage>();
        for(String rss: RSSSources.feeds.keySet()){
            RSSFeedParser parser = new RSSFeedParser(rss);
            Feed feed = parser.readFeed();
            List<FeedMessage> messages = feed.getMessages();
            for(FeedMessage message:messages)
            {
                if(!msgHash.contains(message.getTitle())){   // remove the duplicates
                    msgHash.add(message.getTitle());
                    feedMsgs.add(message);
                }
            }
        }
        List<KeywordBasedFeedRelevanceModel.ScoredFeedMessage> rankedList =
                model.rankFeeds(feedMsgs, Calendar.getInstance().getTime());

        for(KeywordBasedFeedRelevanceModel.ScoredFeedMessage msg: rankedList){
            FeedMessage message = msg.getMsg();
            sb = sb.append("<p><a onclick=\"sendText(this)\" href=\""
                    + message.getLink() + "\">"+message.getTitle()+"</a>"
                    + "(" + msg.getScore() + ")" +
                    "<small>" + message.getDescription() +"</small></p>\n" );

        }
        rankListHTML = sb.toString();

    }

    public void doHandle(String target, Request baseRequest, HttpServletRequest request,
                         HttpServletResponse response) throws IOException, ServletException {
        System.out.println(request.getRequestURL());
        String title = request.getParameter("id");
        System.out.println(title);
        model.addFeed(new FeedMessage(title, null), Calendar.getInstance().getTime());
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Access-Control-Allow-Origin", "*");
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

        FrontPage frontPage = new FrontPage(ajaxHandler.getSectionHTML(), ajaxHandler.getRankListHTML());
        frontPage.setContextPath("/");

        RankedPage rankedPage = new RankedPage(ajaxHandler.getModel(),ajaxHandler.getRankListHTML());
        rankedPage.setContextPath("/rank");

        ContextHandlerCollection handlers = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[] { frontPage, ajaxHandler,cp ,rankedPage});

        server.setHandler(handlers);
        server.start();
        server.join();
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
