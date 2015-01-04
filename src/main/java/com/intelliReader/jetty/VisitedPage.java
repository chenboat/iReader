package com.intelliReader.jetty;

import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.model.ModelUtil;
import com.intelliReader.storage.BerkelyDBStore;
import com.intelliReader.storage.Store;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * User: ting
 * Date: 10/17/14
 * Time: 9:48 PM
 *
 * A page shows the visited feed message title and their visit time
 */
public class VisitedPage  extends ServletContextHandler {
    private final Store<String, Date> visited;
    public VisitedPage(Store<String, Date> v)
    {
        this.visited = v;
    }

    @Override
    public void doHandle(String target,
                         Request baseRequest,
                         HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException
    {
        HTMLUtil.setHTMLPagePrelude(baseRequest,response);

        response.getWriter().println("<table style=\"width:100%\">" +
                "<tr>\n" +
                "    <td>Article</td>\n" +
                "    <td>Date</td>\n" +
                "</tr>");

        try {

            for(String title:visited.getKeys())
            {
                response.getWriter().println(
                        "<tr>\n" +
                                "    <td>" + title +"</td>\n" +
                                "    <td>" + visited.get(title).toString() +"</td>\n" +
                                "</tr>");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().println("</table>");
        HTMLUtil.setHTMLPageEpilogue(response);
    }
}
