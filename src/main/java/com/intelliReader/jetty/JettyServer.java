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
    public static String dbUri = "mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db";

    public Store<String, Date> getVisitedFeedMsgTitleStore() {
        return visitedFeedMsgTitleStore;
    }

    public KeywordBasedFeedRelevanceModel getModel() {
        return model;
    }
    public JettyServer() throws Exception {
        initModel();
    }

    private void initModel() throws Exception {
        // this is a store which store all articles viewed
        visitedFeedMsgTitleStore =  new MongoDBStore<String, Date>(dbUri,"titleTable", "title", "viewDate" );
        model = new KeywordBasedFeedRelevanceModel(
                    new MongoDBStore<String, Double>(dbUri, "scoreTable", "word", "score" ),
                    new MongoDBStore<String, Date>(dbUri, "dateTable", "word", "updateDate"),
                    new StopWordFilter(new MongoDBStore<String, Date>(dbUri,"stopwords","word","time")),
                    new Stemmer());
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
        // Start the content builder thread to build the HTML pages
        (new ContentBuilder()).start();
        //TODO: reduce the number of handlers
        int port = System.getenv("PORT") == null ? 8081 : Integer.valueOf(System.getenv("PORT"));
        Server server = new Server(port);

        JettyServer ajaxHandler = new JettyServer();
        ajaxHandler.setContextPath("/randomBase");

        CountingPage cp = new CountingPage(ajaxHandler.getModel());
        cp.setContextPath("/count");

        VisitedPage visitedPage = new VisitedPage(ajaxHandler.getVisitedFeedMsgTitleStore());
        visitedPage.setContextPath("/v");

        // The login and registration handler which will become the frontpage later
        WebAppContext loginPage = new WebAppContext();
        loginPage.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        loginPage.setResourceBase("src/main/webapp/");
        loginPage.setContextPath("/");
        loginPage.setParentLoaderPriority(true);

        ContextHandlerCollection handlers = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[]
                { ajaxHandler,cp ,visitedPage,loginPage});

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
