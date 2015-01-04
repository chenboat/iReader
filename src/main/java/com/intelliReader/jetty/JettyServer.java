package com.intelliReader.jetty;

import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.model.Stemmer;
import com.intelliReader.model.StopWordFilter;
import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;
import com.intelliReader.newsfeed.RSSSources;
import com.intelliReader.storage.BerkelyDBStore;
import com.intelliReader.storage.MongoDBStore;
import com.intelliReader.storage.Store;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
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
    private Store<String, Date> visitedFeedMsgTitleStore; // this is a store which store all articles viewed
    private String rankListHTML; // this is the HTML which lists all feed messages in reverse scores
    private String sectionHTML; // this is the HTML which lists each feed with its message in a separate section

    public Store<String, Date> getVisitedFeedMsgTitleStore() {
        return visitedFeedMsgTitleStore;
    }

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
        String dbUri = "mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db";
        Store<String, Double> wordScoresStore = new MongoDBStore<String, Double>(dbUri, "scoreTable", "word", "score" );
        Store<String, Date> wordLastUpdatedDatesStore = new MongoDBStore<String, Date>(dbUri, "dateTable", "word", "updateDate");
        visitedFeedMsgTitleStore =  new MongoDBStore<String, Date>(dbUri,"titleTable", "title", "viewDate" );
        System.out.println("Word store size:" + wordScoresStore.getKeys().size());
        model = new KeywordBasedFeedRelevanceModel(
                wordScoresStore,
                wordLastUpdatedDatesStore,
                new StopWordFilter(new MongoDBStore<String, Date>(dbUri,"stopwords","word","time")),
                new Stemmer());

        // Next fetch the latest feed messages and build the html tags
        StringBuffer sb = new StringBuffer();
        for(String rss: RSSSources.feeds.keySet())
        {
            RSSFeedParser parser = new RSSFeedParser(rss);
            try{
                Feed feed = parser.readFeed();
                List<FeedMessage> messages = feed.getMessages();
                sb.append("<p class=\"heading\">");
                sb.append(RSSSources.feeds.get(rss));
                sb.append("</p>\n");
                sb = sb.append("<div class=\"content\">\n");
                for(FeedMessage message:messages)
                {
                    sb.append("<p><a onclick=\"sendText(this)\" href=\"");
                    sb.append(message.getLink());
                    sb.append("\">");
                    sb.append(message.getTitle());
                    sb.append("</a>");
                    sb.append("<small>");
                    sb.append(message.getDescription());
                    sb.append("</small></p>\n");
                }
                sb = sb.append("</div>\n");
            }catch (XMLStreamException e)
            {
            }
        }
        sectionHTML = sb.toString();

        sb = new StringBuffer();

        // Compute the scores for each article and sort the list the scores
        Set<String> msgHash = new HashSet<String>();
        List<FeedMessage> feedMsgs = new ArrayList<FeedMessage>();
        for(String rss: RSSSources.feeds.keySet()){
            try{
                RSSFeedParser parser = new RSSFeedParser(rss);
                Feed feed = parser.readFeed();
                List<FeedMessage> messages = feed.getMessages();
                for(FeedMessage message:messages)
                {
                    if(visitedFeedMsgTitleStore.get(message.getTitle()+ " " + message.getDescription()) == null &&
                            !msgHash.contains(message.getTitle())){
                            // remove the duplicates and visited feed messages
                        msgHash.add(message.getTitle());
                        feedMsgs.add(message);
                    }
                }
            }catch (XMLStreamException e){
            }
        }
        List<KeywordBasedFeedRelevanceModel.ScoredFeedMessage> rankedList =
                model.rankFeeds(feedMsgs, Calendar.getInstance().getTime());
        int topK = 50; // Add pic only to the topK feed msg
        int cnt = 0;
        for(KeywordBasedFeedRelevanceModel.ScoredFeedMessage msg: rankedList){
            FeedMessage message = msg.getMsg();
            Map<String,Double> wordScores = msg.getWordWithScores();
            String tipOverText = getScores(wordScores);
            String picURL;
            if (cnt < topK) {
                picURL = HTMLUtil.getPicURLFromNYTimesLink(message.getLink());
                if(picURL == null){     // if the feed msg does not have a pic, just add the link
                    sb.append("<div class=\"img\">" + "<div class=\"desc\">" + "<a onclick=\"sendText(this)\" href=\"");
                    sb.append(message.getLink());
                    sb.append("\" title=\"");
                    sb.append(tipOverText);
                    sb.append("\">");
                    sb.append(message.getTitle());
                    sb.append("</a>");
                    sb.append("(");
                    sb.append(String.format("%.2f", msg.getScore()));
                    sb.append(")");
                    sb.append("<small>");
                    sb.append(message.getDescription());
                    sb.append("</small>");
                    sb.append("</div>");
                    sb.append("</div>");
                }else{ // otherwise, add both the link and the pic
                    sb.append("<div class=\"img\">" + "<a>\n" + "    <img src=\"");
                    sb.append(picURL);
                    sb.append("\" width=\"140\" height=\"114\">\n");
                    sb.append("</a>");
                    sb.append("<div class=\"desc\">");
                    sb.append("<a onclick=\"sendText(this)\" href=\"");
                    sb.append(message.getLink());
                    sb.append("\" title=\"");
                    sb.append(tipOverText);
                    sb.append("\">");
                    sb.append(message.getTitle());
                    sb.append("</a>");
                    sb.append("(");
                    sb.append(String.format("%.2f", msg.getScore()));
                    sb.append(")");
                    sb.append("<small>");
                    sb.append(message.getDescription());
                    sb.append("</small>");
                    sb.append("</div>");
                    sb.append("</div>");
                }
            }else {
                sb.append("<p><a onclick=\"sendText(this)\" href=\"");
                sb.append(message.getLink());
                sb.append("\" title=\"");
                sb.append(tipOverText);
                sb.append("\">");
                sb.append(message.getTitle());
                sb.append("</a>");
                sb.append("(");
                sb.append(String.format("%.2f", msg.getScore()));
                sb.append(")");
                sb.append("<small>");
                sb.append(message.getDescription());
                sb.append("</small></p>\n");
            }
            cnt++;
        }
        rankListHTML = sb.toString();

    }

    private String getScores(Map<String,Double> wordScores) {
        StringBuffer sb = new StringBuffer();
        List<Pair> lst = new ArrayList<Pair>();
        for(String s: wordScores.keySet()){
            lst.add(new Pair(s,wordScores.get(s)));
        }
        Collections.sort(lst);
        for(Pair p: lst){
            sb = sb.append(p.k).append(":").append(p.s).append("|");
        }
        return sb.toString();
    }

    public void doHandle(String target, Request baseRequest, HttpServletRequest request,
                         HttpServletResponse response) throws IOException, ServletException {
        System.out.println(request.getRequestURL());
        String title = request.getParameter("id");
        System.out.println(title);
        Date date = Calendar.getInstance().getTime();
        model.addFeed(new FeedMessage(title, null), date);
        try {
            visitedFeedMsgTitleStore.put(title, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.visitedFeedMsgTitleStore.sync();
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
        int port = System.getenv("PORT") == null ? 8081 : Integer.valueOf(System.getenv("PORT"));
        Server server = new Server(port);

        JettyServer ajaxHandler = new JettyServer();
        ajaxHandler.setContextPath("/randomBase");

        CountingPage cp = new CountingPage(ajaxHandler.getModel());
        cp.setContextPath("/count");

        FrontPage frontPage = new FrontPage(ajaxHandler.getSectionHTML(), ajaxHandler.getRankListHTML());
        frontPage.setContextPath("/");

        RankedPage rankedPage = new RankedPage(ajaxHandler.getModel(),ajaxHandler.getRankListHTML());
        rankedPage.setContextPath("/rank");

        VisitedPage visitedPage = new VisitedPage(ajaxHandler.getVisitedFeedMsgTitleStore());
        visitedPage.setContextPath("/v");

        // The stop word store handler
        WebAppContext stopWordPage = new WebAppContext();
        stopWordPage.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        stopWordPage.setResourceBase("src/main/webapp/");
        stopWordPage.setContextPath("/s");
        stopWordPage.setParentLoaderPriority(true);

        ContextHandlerCollection handlers = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[] { frontPage, ajaxHandler,cp ,rankedPage,visitedPage, stopWordPage});

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
