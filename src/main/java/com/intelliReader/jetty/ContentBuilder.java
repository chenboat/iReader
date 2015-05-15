package com.intelliReader.jetty;

import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.model.Stemmer;
import com.intelliReader.model.StopWordFilter;
import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;
import com.intelliReader.newsfeed.RSSSources;
import com.intelliReader.storage.MongoDBConnections;
import com.intelliReader.storage.MongoDBStore;
import com.intelliReader.storage.Store;
import com.intelliReader.util.StringUtil;

import javax.xml.stream.XMLStreamException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
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
        Map<String,String> msgHash = new HashMap<String,String>();
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
                        msgHash.put(message.getTitle(), extractSection(RSSSources.feeds.get(rss)));
                        feedMsgs.add(message);
                    }
                }
            }catch (XMLStreamException e){
                e.printStackTrace();
            }
        }
        List<KeywordBasedFeedRelevanceModel.ScoredFeedMessage> rankedList =
                model.rankFeeds(feedMsgs, Calendar.getInstance().getTime());
        sb.append("<div id=\"columns\">");
        for(KeywordBasedFeedRelevanceModel.ScoredFeedMessage msg: rankedList){
            FeedMessage message = msg.getMsg();
            Map<String,Double> wordScores = msg.getWordWithScores();
            String tipOverText = getScores(wordScores,userId);
            String picURL;
            picURL = HTMLUtil.getPicURLFromNYTimesLink(message.getLink());
            if(picURL != null){     // only add articles having pics
                String section = msgHash.get(message.getTitle());
                sb.append("<figure>" + "    <img src=\"");
                sb.append(picURL);
                sb.append("\">\n");
                sb.append("<figcaption>");
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
                sb.append("</figcaption>");
                sb.append("</figure>");
            }
        }
        sb.append("</div>");
        String rankListHTML = sb.toString();

        MongoDBConnections.accountRankingHTMLStore.put(userId,rankListHTML);
    }


    private void initSectionModel(String userId){
        log.info("Build the html page @ " +  Calendar.getInstance());
        try {
            // First load the word and score info stored in bdb
            String dbUri = "mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db";
            Store<String, String> sectionHTMLStore = new MongoDBStore<String, String>(dbUri, "sectionTable", "field", "value");
            // Next fetch the latest feed messages and build the html tags
            StringBuffer sb = new StringBuffer();
            sb.append("<div id=\"sections\">");
            for (String rss : RSSSources.feeds.keySet()) {
                String section = extractSection(RSSSources.feeds.get(rss));
                RSSFeedParser parser = new RSSFeedParser(rss);
                try {
                    Feed feed = parser.readFeed();
                    List<FeedMessage> messages = feed.getMessages();
                    sb.append("<p class=\"heading\">");
                    sb.append(RSSSources.feeds.get(rss) + " [+]");
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

    private String extractSection(String s) {
        return s.substring("NYTimes ".length());
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
