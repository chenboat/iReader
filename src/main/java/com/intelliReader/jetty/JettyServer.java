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
    public static void main(String[] args) throws Exception {
        // Start the content builder thread to build the HTML pages
        (new ContentBuilder()).start();
        //TODO: reduce the number of handlers
        int port = System.getenv("PORT") == null ? 8081 : Integer.valueOf(System.getenv("PORT"));
        Server server = new Server(port);

        // The login and registration handler which will become the frontpage later
        WebAppContext loginPage = new WebAppContext();
        loginPage.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        loginPage.setResourceBase("src/main/webapp/");
        loginPage.setContextPath("/");
        loginPage.setParentLoaderPriority(true);

        ContextHandlerCollection handlers = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[]{loginPage});

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
