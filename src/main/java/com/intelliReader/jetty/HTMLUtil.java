package com.intelliReader.jetty;

import org.eclipse.jetty.server.Request;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 8/31/14
 * Time: 3:39 PM
 */
public class HTMLUtil {
    public static void setHTMLPagePrelude(Request baseRequest,
                                          HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<html>\n <body>");
    }

    public static void setHTMLPageEpilogue(HttpServletResponse response) throws IOException {
        response.getWriter().println("</body>\n" + "</html>");
    }
}
