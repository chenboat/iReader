package com.intelliReader.jetty;

import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.model.ModelUtil;
import com.intelliReader.storage.Store;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 9/1/14
 * Time: 4:29 PM
 */
class CountingPage extends ServletContextHandler {
    private final KeywordBasedFeedRelevanceModel model;
    public CountingPage(KeywordBasedFeedRelevanceModel m)
    {
        model = m;
    }

    @Override
    public void doHandle(String target,
                         Request baseRequest,
                         HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException
    {
        HTMLUtil.setHTMLPagePrelude(baseRequest,response);
        Store<String,Double> wordScores = model.getWordScores();
        Store<String,Date> wordDates = model.getWordLastUpdatedDates();

        response.getWriter().println("<table style=\"width:100%\">" +
                                     "<tr>\n" +
                                     "    <td>Word (Stemmed)</td>\n" +
                                     "    <td>Score</td>\n" +
                                     "    <td>Raw Score</td>\n" +
                                     "    <td>Last Time Viewed</td>\n" +
                                     "</tr>");

        try {
            List<Pair> lst = new ArrayList<Pair>();
            for(String k: wordScores.getKeys())
            {
                lst.add(new Pair(k,
                                ModelUtil.exponentialDecayScore(wordScores.get(k),
                                        wordDates.get(k),
                                        Calendar.getInstance().getTime())));
            }
            Collections.sort(lst);


            for(Pair p: lst)
            {
                response.getWriter().println(
                        "<tr>\n" +
                        "    <td>" + p.k +"</td>\n" +
                        "    <td>" + p.s +"</td>\n" +
                        "    <td>" + wordScores.get(p.k) +"</td>\n" +
                        "    <td>" + wordDates.get(p.k) +"</td>\n" +
                        "</tr>");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().println("</table>");
        HTMLUtil.setHTMLPageEpilogue(response);
    }
}
