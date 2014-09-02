package com.intelliReader.jetty;

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
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 9/1/14
 * Time: 4:34 PM
 */
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
        response.setHeader("Access-Control-Allow-Origin", "*");
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
            "    <script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js\"></script>\n" +
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
            "        function sendText(id) {\n" +
            "            var title=id.firstChild.data;\n" +
            "            var base = \"./randomBase?t=\" + Math.random() + \"&id=\";\n" +
            "            var uri = base.concat(title);\n" +
            "            $.ajax({type: \"GET\", url: uri,async: false,error: function(xhr, error){\n" +
            "               console.debug(xhr); console.debug(error);\n" +
            "               }});" +
            "     }\n" +
            "    </script>\n" +
            "</head>\n" +
            "<body>" +
            "<div style=\"column-count:4;-moz-column-count:4; /* Firefox */\n" +
            "-webkit-column-count:4; /* Safari and Chrome */\">";

}
