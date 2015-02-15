package com.intelliReader.jetty;

import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.model.Stemmer;
import com.intelliReader.model.StopWordFilter;
import com.intelliReader.newsfeed.Feed;
import com.intelliReader.newsfeed.FeedMessage;
import com.intelliReader.newsfeed.RSSFeedParser;
import com.intelliReader.newsfeed.RSSSources;
import com.intelliReader.storage.MongoDBStore;
import com.intelliReader.storage.Store;

import javax.xml.stream.XMLStreamException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * User: ting
 * Date: 2/14/2015
 * Time: 2:48 PM
 * Build the content for RSS feeds and write the contents to a persistent storage
 */
public class ContentBuilder extends Thread {
    public static final int TOP_K_ARTICLES = 300;
    public static final String SECTION_HTML_COLOUMN_NAME = "html";
    public static final String RANKING_HTML_COLUMN_NAME = "html";
    Logger log = Logger.getLogger(ContentBuilder.class.getName());
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public void run() {
        while(true){
            initSectionModel();
            try {
                initRankingModel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(1800 * 1000);  // sleep 1800 sec
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void initRankingModel() throws Exception{
        StringBuilder sb = new StringBuilder();
        KeywordBasedFeedRelevanceModel model;
         // this is a store which store all articles viewed
        String dbUri = "mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db";
        Store<String, Double> wordScoresStore = new MongoDBStore<String, Double>(dbUri, "scoreTable", "word", "score" );
        Store<String, Date> wordLastUpdatedDatesStore = new MongoDBStore<String, Date>(dbUri, "dateTable", "word", "updateDate");
        Store<String, Date> visitedFeedMsgTitleStore =  new MongoDBStore<String, Date>(dbUri,"titleTable", "title", "viewDate" );
        System.out.println("Word store size:" + wordScoresStore.getKeys().size());
        model = new KeywordBasedFeedRelevanceModel(
                wordScoresStore,
                wordLastUpdatedDatesStore,
                new StopWordFilter(new MongoDBStore<String, Date>(dbUri,"stopwords","word","time")),
                new Stemmer());

        // Compute the scores for each article and sort the list the scores
        Set<String> msgHash = new HashSet<String>();
        List<FeedMessage> feedMsgs = new ArrayList<FeedMessage>();
        for(String rss: RSSSources.feeds.keySet()){
            try{
                RSSFeedParser parser = new RSSFeedParser(rss);
                Feed feed = parser.readFeed();
                List<FeedMessage> messages = feed.getMessages();
                for(FeedMessage message:messages)
                {
                    if(visitedFeedMsgTitleStore.get(message.getTitle()+ " " + message.getDescription()) == null &&
                            !msgHash.contains(message.getTitle())){
                        // remove the duplicates and visited feed messages
                        msgHash.add(message.getTitle());
                        feedMsgs.add(message);
                    }
                }
            }catch (XMLStreamException e){
                e.printStackTrace();
            }
        }
        List<KeywordBasedFeedRelevanceModel.ScoredFeedMessage> rankedList =
                model.rankFeeds(feedMsgs, Calendar.getInstance().getTime());
        int topK = TOP_K_ARTICLES; // Add pic only to the topK feed msg
        int cnt = 0;
        for(KeywordBasedFeedRelevanceModel.ScoredFeedMessage msg: rankedList){
            FeedMessage message = msg.getMsg();
            Map<String,Double> wordScores = msg.getWordWithScores();
            String tipOverText = getScores(wordScores);
            String picURL;
            if (cnt < topK) {
                picURL = HTMLUtil.getPicURLFromNYTimesLink(message.getLink());
                if(picURL == null){     // if the feed msg does not have a pic, just add the link
                    sb.append("<div class=\"img\">" + "<div class=\"desc\">" + "<a onclick=\"sendText(this)\" href=\"");
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
                }else{ // otherwise, add both the link and the pic
                    sb.append("<div class=\"img\">" + "<a>\n" + "    <img src=\"");
                    sb.append(picURL);
                    sb.append("\" width=\"140\" height=\"114\">\n");
                    sb.append("</a>");
                    sb.append("<div class=\"desc\">");
                    sb.append("<a onclick=\"sendText(this)\" href=\"");
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
            }else {
                sb.append("<p><a onclick=\"sendText(this)\" href=\"");
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
                sb.append("</small></p>\n");
            }
            cnt++;
        }
        String rankListHTML = sb.toString();
        Store<String, String> rankingHTMLStore =
                new MongoDBStore<String, String>(dbUri, "rankingHTMLTable", "field", "value");
        rankingHTMLStore.put(RANKING_HTML_COLUMN_NAME,rankListHTML);
        rankingHTMLStore.put("time",df.format(new Date()));
    }
    private void initSectionModel(){
        log.info("Build the html page @ " +  Calendar.getInstance());
        try {
            // First load the word and score info stored in bdb
            String dbUri = "mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db";
            Store<String, String> sectionHTMLStore = new MongoDBStore<String, String>(dbUri, "sectionTable", "field", "value");
            // Next fetch the latest feed messages and build the html tags
            StringBuffer sb = new StringBuffer();
            for (String rss : RSSSources.feeds.keySet()) {
                RSSFeedParser parser = new RSSFeedParser(rss);
                try {
                    Feed feed = parser.readFeed();
                    List<FeedMessage> messages = feed.getMessages();
                    sb.append("<p class=\"heading\">");
                    sb.append(RSSSources.feeds.get(rss));
                    sb.append("</p>\n");
                    sb = sb.append("<div class=\"content\">\n");
                    for (FeedMessage message : messages) {
                        sb.append("<p><a onclick=\"sendText(this)\" href=\"");
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
            String sectionHTML = sb.toString();
            sectionHTMLStore.put(SECTION_HTML_COLOUMN_NAME, sectionHTML);
            sectionHTMLStore.put("time", df.format(new Date()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getScores(Map<String,Double> wordScores) {
        StringBuffer sb = new StringBuffer();
        List<Pair> lst = new ArrayList<Pair>();
        for(String s: wordScores.keySet()){
            lst.add(new Pair(s,wordScores.get(s)));
        }
        Collections.sort(lst);
        for(Pair p: lst){
            sb = sb.append(p.k).append(":").append(p.s).append("|");
        }
        return sb.toString();
    }
}
