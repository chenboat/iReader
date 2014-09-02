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

    public RankedPage(KeywordBasedFeedRelevanceModel model)
    {
        this.model = model;
    }
    @Override
    public void doHandle(String target,
                         Request baseRequest,
                         HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException
    {
        HTMLUtil.setHTMLPagePrelude(baseRequest,response);
        // Compute the scores for each article and sort the list the scores
        List<FeedMessage> feedMsgs = new ArrayList<FeedMessage>();
        for(String rss: RSSSources.feeds.keySet()){
            RSSFeedParser parser = new RSSFeedParser(rss);
            Feed feed = parser.readFeed();
            List<FeedMessage> messages = feed.getMessages();
            for(FeedMessage message:messages)
            {
                feedMsgs.add(message);
            }
        }
        List<KeywordBasedFeedRelevanceModel.ScoredFeedMessage> rankedList =
                model.rankFeeds(feedMsgs, Calendar.getInstance().getTime());

        for(KeywordBasedFeedRelevanceModel.ScoredFeedMessage msg: rankedList){
            FeedMessage message = msg.getMsg();
            response.getWriter().println("<p><a href=\"" + message.getLink() + "\">"+message.getTitle()+"</a>" +
                    "(" + msg.getScore() + ")" +
                    "<small>" + message.getDescription() +"</small></p>" );

        }

        HTMLUtil.setHTMLPageEpilogue(response);
    }

}