package com.intelliReader.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 9/1/14
 * Time: 4:34 PM
 */
class FrontPage extends ServletContextHandler {
    private final String sectionHTML;
    private final String rankedListHTML; // the HTML for a ranked list of feed messages

    public FrontPage(String sectionHtml, String rHTML){
        sectionHTML = sectionHtml;
        rankedListHTML = rHTML;
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
        response.setHeader("Access-Control-Allow-Origin", "*");
        baseRequest.setHandled(true);
        response.getWriter().println(htmlPageHeader);
        response.getWriter().println(sectionHTML);
        response.getWriter().println("</div><div>" + rankedListHTML + "</div></body>\n" + "</html>");
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
            "<div style=\"column-count:2;-moz-column-count:2; /* Firefox */\n" +    // the container div which has
            "-webkit-column-count:2; /* Safari and Chrome */\">" +                  // two divisions: sections and rank
            "<div style=\"column-count:4;-moz-column-count:4; /* Firefox */\n" +    // the first nested div for sections
            "-webkit-column-count:4; /* Safari and Chrome */\">";

}
