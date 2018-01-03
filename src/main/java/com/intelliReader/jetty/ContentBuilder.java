package com.intelliReader.jetty;

import com.intelliReader.image.LabelImage;
import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.model.Stemmer;
import com.intelliReader.model.StopWordFilter;
import com.intelliReader.newsfeed.*;
import com.intelliReader.storage.MongoDBConnections;
import com.intelliReader.storage.MongoDBStore;
import com.intelliReader.storage.Store;
import com.intelliReader.util.StringUtil;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: ting
 * Date: 2/14/2015
 * Time: 2:48 PM
 * Build the content for RSS feeds and write the contents to a persistent storage
 */
public class ContentBuilder extends Thread {
    public static final String SECTION_HTML_COLOUMN_NAME = "html";
    public static final String RANKING_HTML_COLUMN_NAME = "html";
    Logger log = Logger.getLogger(ContentBuilder.class.getName());
    public void run() {
        while(true){
            try {
                buildContentsForAllAccounts();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(3600 * 1000);  // sleep 3600 sec
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void buildContentsForAllAccounts() throws Exception{
        for(String userEmail : MongoDBConnections.accountsTable.getAll().keySet()){
            log.info("Build the html content for user " + userEmail);
            initSectionModel(userEmail);
            initRankingModelInPinterestStyle(userEmail);
        }
    }
    private void initRankingModelInPinterestStyle(String userId) throws Exception{
        StringBuilder sb = new StringBuilder();
        KeywordBasedFeedRelevanceModel model = new KeywordBasedFeedRelevanceModel(
                MongoDBConnections.scoreTable,
                MongoDBConnections.dateTable,
                new StopWordFilter(MongoDBConnections.stopwordTable,userId),
                new Stemmer(),
                userId);

        // Compute the scores for each article and sort the list by the scores
        Map<String,RSSFeedDescriptor> msgHash = new HashMap<String,RSSFeedDescriptor>();
        List<FeedMessage> feedMsgs = new ArrayList<FeedMessage>();
        for(String rss: RSSSources.feeds.keySet()){
            try{
                RSSFeedParser parser = new RSSFeedParser(rss);
                Feed feed = parser.readFeed();
                List<FeedMessage> messages = feed.getMessages();
                for(FeedMessage message:messages)
                {
                    if(MongoDBConnections.visitedFeedMsgTitleStore.get
                            (StringUtil.makeSSTableKey(userId,message.getTitle()+" "+ message.getDescription())) == null
                            &&
                            !msgHash.containsKey(message.getTitle())){
                        // remove the duplicates and visited feed messages
                        msgHash.put(message.getTitle(), RSSSources.feeds.get(rss));
                        feedMsgs.add(message);
                    }
                }
            }catch (XMLStreamException e){
                e.printStackTrace();
            }
        }
        List<KeywordBasedFeedRelevanceModel.ScoredFeedMessage> rankedList =
                model.rankFeeds(feedMsgs, Calendar.getInstance().getTime());
        sb.append("<div class=\"w3-row-padding\">");
        LabelImage LABEL_IMAGE = new LabelImage(
                getClass().getResourceAsStream("/iReader/inception5h/tensorflow_inception_graph.pb"),
                getClass().getResourceAsStream("/iReader/inception5h/imagenet_comp_graph_label_strings.txt"),3);
        for(KeywordBasedFeedRelevanceModel.ScoredFeedMessage msg: rankedList){
            FeedMessage message = msg.getMsg();
            Map<String,Double> wordScores = msg.getWordWithScores();
            String tipOverText = getScores(wordScores,userId);
            String picURL;
            picURL = msgHash.get(message.getTitle()).getPictureUrl(message);
            if(picURL != null){     // only add articles having pics
                String section = msgHash.get(message.getTitle()).getCategory();
                // Add the pic labels to the tipOverText.
                tipOverText = getString(LABEL_IMAGE, tipOverText, picURL);
                log.log(Level.WARNING, msg.getMsg().getTitle() + "|" + picURL + "|" + tipOverText);
                builArticleWithPic(userId, sb, msg, message, tipOverText, picURL, section);
            }
        }
        sb.append("</div>");
        String rankListHTML = sb.toString();

        MongoDBConnections.accountRankingHTMLStore.put(userId,rankListHTML);
    }

    private String getString(LabelImage LABEL_IMAGE, String tipOverText, String picURL) {
        List<String> imageLabels = LABEL_IMAGE.getLabels(picURL);
        if (imageLabels != null) {
            tipOverText += "<";
            for (String l : imageLabels) {
                tipOverText += (l + ",");
            }
            tipOverText += ">";
        }
        return tipOverText;
    }

    private void builArticleWithPic(String userId,
                                    StringBuilder sb,
                                    KeywordBasedFeedRelevanceModel.ScoredFeedMessage msg,
                                    FeedMessage message,
                                    String tipOverText,
                                    String picURL,
                                    String section) {
        sb.append("<div class=\"w3-card-4 w3-quarter w3-padding-16\" section=\"" + section +  "\">");
        sb.append("<img src=\"");
        sb.append(picURL);
        sb.append("\" style=\"width:100%;max-width:400px\">\n");
        sb.append("<div class=\"w3-container\">");
        sb.append("<a onclick=\"sendText(this,'" + userId  + "','" + section + "')\" href=\"");
        sb.append(message.getLink());
        sb.append("\" title=\"");
        sb.append(tipOverText);
        sb.append("\">");
        sb.append(message.getTitle());
        sb.append("</a>");
        sb.append("(");
        sb.append(String.format("%.2f", msg.getScore()));
        sb.append(")");
        sb.append("<small>");
        sb.append(message.getDescription());
        sb.append("</small>");
        sb.append("</div>");
        sb.append("</div>");
    }


    private void initSectionModel(String userId){
        log.info("Build the html page @ " +  Calendar.getInstance());
        try {
            // Fetch the latest feed messages and build the html tags
            StringBuffer sb = new StringBuffer();
            sb.append("<div id=\"sections\">");
            for (String rss : RSSSources.feeds.keySet()) {
                String section = RSSSources.feeds.get(rss).getCategory();
                RSSFeedParser parser = new RSSFeedParser(rss);
                try {
                    Feed feed = parser.readFeed();
                    List<FeedMessage> messages = feed.getMessages();
                    sb.append("<p section=\"" + section + "\">");
                    sb.append("<span class=\"heading\">" + RSSSources.feeds.get(rss).getCategory() + " [+]</span>" +
                            "<span onclick=\"up(this)\"> &uarr;&nbsp; </span>");
                    sb.append("</p>\n");
                    sb = sb.append("<div class=\"content\">\n");
                    for (FeedMessage message : messages) {
                        sb.append("<p><a onclick=\"sendText(this,'" + userId + "','" + section + "')\" href=\"");
                        sb.append(message.getLink());
                        sb.append("\">");
                        sb.append(message.getTitle());
                        sb.append("</a>");
                        sb.append("<small>");
                        sb.append(message.getDescription());
                        sb.append("</small></p>\n");
                    }
                    sb = sb.append("</div>\n");
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
            sb.append("</div>");
            String sectionHTML = sb.toString();
            MongoDBConnections.accountSectionHTMLStore.put(userId, sectionHTML);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getScores(Map<String,Double> wordScores, String userId) {
        StringBuffer sb = new StringBuffer();
        List<Pair> lst = new ArrayList<Pair>();
        for(String s: wordScores.keySet()){
            lst.add(new Pair(s,wordScores.get(s)));
        }
        Collections.sort(lst);
        for(Pair p: lst){
            String word = p.k.substring(userId.length() + 1);
            sb = sb.append(word).append(":").append(String.format("%.2f",p.s)).append("|");
        }
        return sb.toString();
    }
}
