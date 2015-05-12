package com.intelliReader.storage;

import java.util.Date;

/**
 * User: ting
 * Date: 3/1/2015
 * Time: 1:59 PM
 * A utility to list all connections to the MongoDB cloud service
 */
public class MongoDBConnections {
    public static String dbUri = "mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db";
    public static MongoDBStore<String,String> accountHTMLTable;       // should be deprecated
    public static MongoDBMapStore<String> visitedFeedMsgTitleStore;
    public static MongoDBStore<String,Double> scoreTable;
    public static MongoDBStore<String,Date> dateTable;
    public static MongoDBStore<String,Date> stopwordTable;
    public static MongoDBStore<String,Integer> accountsTable;
    public static MongoDBStore<String,String> accountRankingHTMLStore;
    public static MongoDBStore<String,String> accountSectionHTMLStore;

    static {
        try {
            accountHTMLTable = new MongoDBStore<String, String>(dbUri, "accountHTMLTable", "userId", "html");
            // this is a store which store all articles viewed
            visitedFeedMsgTitleStore =  new MongoDBMapStore<String>(dbUri,"readArticlesTable", "title");
            scoreTable = new MongoDBStore<String, Double>(dbUri, "scoreTable", "word", "score" );
            dateTable = new MongoDBStore<String, Date>(dbUri, "dateTable", "word", "updateDate");
            stopwordTable = new MongoDBStore<String, Date>(dbUri,"accountStopwords","word","time");
            accountsTable = new MongoDBStore<String, Integer>(dbUri,"accounts","email","userId");
            accountRankingHTMLStore =
                    new MongoDBStore<String, String>(dbUri, "accountRankingHTMLTable", "userId", "html");
            accountSectionHTMLStore =
                    new MongoDBStore<String, String>(dbUri, "accountSectionHTMLStore", "userId", "html");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
