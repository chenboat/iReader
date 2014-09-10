package com.intelliReader.jetty;

import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;
import com.intelliReader.newsfeed.RSSSources;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 9/1/14
 * Time: 4:32 PM
 *
 * This class models the ranked list of RSS feed messages.
 */
class RankedPage extends ServletContextHandler {
    private final KeywordBasedFeedRelevanceModel model;
    private final String rankHTML;

    public RankedPage(KeywordBasedFeedRelevanceModel model, String html)
    {
        this.model = model;
        this.rankHTML = html;
    }
    @Override
    public void doHandle(String target,
                         Request baseRequest,
                         HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException
    {
        HTMLUtil.setHTMLPagePrelude(baseRequest,response);
        response.getWriter().println(rankHTML);
        HTMLUtil.setHTMLPageEpilogue(response);
    }

}