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

        FrontPage frontPage = new FrontPage();
        frontPage.setContextPath("/");

        RankedPage rankedPage = new RankedPage(ajaxHandler.getModel());
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
