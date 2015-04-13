package com.intelliReader.jetty;

import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.model.Stemmer;
import com.intelliReader.model.StopWordFilter;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.storage.MongoDBConnections;
import com.intelliReader.util.StringUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Calendar;
import java.util.Date;

/**
 * User: ting
 * Date: 2/28/2015
 * Time: 8:40 PM
 *
 * A resource to process user account related data
 */
@Path("/account")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class AccountResource{
    public AccountResource(){

    }

    @POST
    @Path("/sectionsHtml")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String getAccountSectionsHTMLPage( @FormParam("id") String id) throws Exception {
      return MongoDBConnections.accountSectionHTMLStore.get(id);
    }

    @POST
    @Path("/rankingHtml")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String getAccountRankingHTMLPage( @FormParam("id") String id) throws Exception {
        return MongoDBConnections.accountRankingHTMLStore.get(id);
    }

    @POST
    @Path("/record")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void recordUserReads(@FormParam("id") String id, @FormParam("title") String title){
        KeywordBasedFeedRelevanceModel model = new KeywordBasedFeedRelevanceModel(
                MongoDBConnections.scoreTable,
                MongoDBConnections.dateTable,
                new StopWordFilter(MongoDBConnections.stopwordTable,id),
                new Stemmer(),
                id);

        Date date = Calendar.getInstance().getTime();
        model.addFeed(new FeedMessage(title, null), date);
        try {
            MongoDBConnections.visitedFeedMsgTitleStore.put(StringUtil.makeSSTableKey(id,title), date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
