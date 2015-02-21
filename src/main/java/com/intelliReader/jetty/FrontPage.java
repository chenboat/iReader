package com.intelliReader.jetty;

import com.intelliReader.storage.MongoDBStore;
import com.intelliReader.storage.Store;
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
    public FrontPage(){
    }
    @Override
    public void doHandle(String target,
                         Request baseRequest,
                         HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException
    {
        Store<String, String> sectionHTMLStore = new MongoDBStore<String, String>
                (JettyServer.dbUri, "sectionTable", "field", "value");
        String sectionHTML = null;
        try {
            sectionHTML = sectionHTMLStore.get(ContentBuilder.SECTION_HTML_COLOUMN_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: add graceful recovery routine
        }
        Store<String, String> rankingHTMLStore =
                new MongoDBStore<String, String>(JettyServer.dbUri, "rankingHTMLTable", "field", "value");
        String rankListHTML = null;
        try {
            rankListHTML = rankingHTMLStore.get(ContentBuilder.RANKING_HTML_COLUMN_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: add graceful recovery routine
        }

        Store<String, String> pinterestHTMLStore =
                new MongoDBStore<String, String>(JettyServer.dbUri, "pinterestHTMLTable", "field", "value");
        String pinterestHTML = null;
        try {
            pinterestHTML = pinterestHTMLStore.get(ContentBuilder.RANKING_HTML_COLUMN_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: add graceful recovery routine
        }

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Access-Control-Allow-Origin", "*");
        baseRequest.setHandled(true);
        response.getWriter().println(htmlPageHeader);
        response.getWriter().println(sectionHTML);
        response.getWriter().println("</div>" +
                                    Div4Col + pinterestHTML + "</div>" +
                                    "</div></body>\n" + "</html>");
    }
    private static String Div4Col = "<div style=\"column-count:4;-moz-column-count:4; /* Firefox */\n" +
                                        "-webkit-column-count:4; /* Safari and Chrome */\">";

    private static String pinStyle = "#columns {\n" +
            "\tcolumn-width: 320px;\n" +
            "\tcolumn-gap: 15px;\n" +
            "  width: 90%;\n" +
            "\tmax-width: 1100px;\n" +
            "\tmargin: 50px auto;\n" +
            "}\n" +
            "\n" +
            "div#columns figure {\n" +
            "\tbackground: #fefefe;\n" +
            "\tborder: 2px solid #fcfcfc;\n" +
            "\tbox-shadow: 0 1px 2px rgba(34, 25, 25, 0.4);\n" +
            "\tmargin: 0 2px 15px;\n" +
            "\tpadding: 15px;\n" +
            "\tpadding-bottom: 10px;\n" +
            "\ttransition: opacity .4s ease-in-out;\n" +
            "  display: inline-block;\n" +
            "  column-break-inside: avoid;\n" +
            "}\n" +
            "\n" +
            "div#columns figure img {\n" +
            "\twidth: 100%; height: auto;\n" +
            "\tborder-bottom: 1px solid #ccc;\n" +
            "\tpadding-bottom: 15px;\n" +
            "\tmargin-bottom: 5px;\n" +
            "}\n" +
            "\n" +
            "div#columns figure figcaption {\n" +
            "  font-size: .9rem;\n" +
            "\tcolor: #444;\n" +
            "  line-height: 1.5;\n" +
            "}\n" +
            "\n" +
            "div#columns small { \n" +
            "  font-size: 1rem;\n" +
            "  float: right; \n" +
            "  color: #aaa;\n" +
            "} \n" +
            "\n" +
            "div#columns small a { \n" +
            "  color: #666; \n" +
            "  text-decoration: none; \n" +
            "  transition: .4s color;\n" +
            "}";

    private static String imageCSSStyle = "<style>\n" +
            "div.img {\n" +
            "    margin: 5px;\n" +
            "    padding: 5px;\n" +
            "    border: 1px solid #0000ff;\n" +
            "    height: auto;\n" +
            "    width: auto;\n" +
            "    float: left;\n" +
            "    text-align: center;\n" +
            "}\n" +
            "\n" +
            "div.img img {\n" +
            "    display: inline;\n" +
            "    margin: 5px;\n" +
            "    border: 1px solid #ffffff;\n" +
            "}\n" +
            "\n" +
            "div.img a:hover img {\n" +
            "    border:1px solid #0000ff;\n" +
            "}\n" +
            "\n" +
            "div.desc {\n" +
            "    text-align: left;\n" +
            "    font-weight: normal;\n" +
            "    width: 150px;\n" +
            "    margin: 5px;\n" +
            "}\n" + pinStyle +
            "</style>";

    private static String htmlPageHeader = "<!DOCTYPE html><html>\n" +
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
            "            var title=id.firstChild.data + \" \" + id.parentNode.lastChild.textContent;\n" +
            "            var base = \"./randomBase?t=\" + Math.random() + \"&id=\";\n" +
            "            var uri = base.concat(title);\n" +
            "            $.ajax({type: \"GET\", url: uri,async: false,error: function(xhr, error){\n" +
            "               console.debug(xhr); console.debug(error);\n" +
            "               }});" +
            "     }\n" +
            "    </script>\n" +
            imageCSSStyle +
            "</head>\n" +
            "<body>" +
            "<div style=\"column-count:1;-moz-column-count:1; /* Firefox */\n" +    // the container div which has
            "-webkit-column-count:1; /* Safari and Chrome */\">" +                  // 2 divisions: sections and rank
            Div4Col;

}
