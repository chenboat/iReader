package mangodb;

import com.intelliReader.jetty.StopwordResource;
import com.intelliReader.storage.BerkelyDBStore;
import junit.framework.TestCase;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: ting
 * Date: 1/1/2015
 * Time: 12:01 PM
 */
public class TestJavaDriver extends TestCase {

    public static List<BasicDBObject> createSeedData() throws Exception {
        List<BasicDBObject> seedData = new ArrayList<BasicDBObject>();
        for(String s: StopwordResource.store.getKeys())
        {
            BasicDBObject obj = new BasicDBObject();
            obj.put("word",s);
            obj.put("time",StopwordResource.store.get(s));
            seedData.add(obj);
        }
        return seedData;
    }
    public void testMongoDBConnection() throws Exception{

        // Create seed data
        //final List<BasicDBObject> seedData = createSeedData();
        MongoClientURI uri  = new MongoClientURI("mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db");
        MongoClient client = new MongoClient(uri);
        DB db = client.getDB(uri.getDatabase());

        /*
         * First we'll add a few songs. Nothing is required to create the
         * songs collection; it is created automatically when we insert.
         */

        DBCollection stopwords = db.getCollection("stopwords");

        // Note that the insert method can take either an array or a document.
        //stopwords.insert(seedData.toArray(new BasicDBObject[0]));
        DBCursor docs = stopwords.find();

        while(docs.hasNext()){
            DBObject doc = docs.next();
            System.out.println(doc.get("word") + ", " + doc.get("time")
            );
        }

        // Since this is an example, we'll clean up after ourselves.

        //stopwords.drop();

        // Only close the connection when your app is terminating

        client.close();
    }
    // IMPORTANT: the following is an on-off loading script and should NOT be part of a test suite. Otherwise, it will
    // cause duplicates in the date set
    /*public void testInitData() throws Exception {
        String projRoot = System.getProperty("user.dir");
        // First load the word and score info stored in bdb
        String dbPath = projRoot + "/src/main/resources/iReader";
        System.out.println("DBPath:" + dbPath);
        BerkelyDBStore<String, Double> wordScoresStore =
                new BerkelyDBStore<String, Double>(dbPath, String.class, Double.class, "scoreTable" );
        BerkelyDBStore<String, Date> wordLastUpdatedDatesStore =
                new BerkelyDBStore<String, Date>(dbPath, String.class, Date.class, "dateTable");
        BerkelyDBStore<String, Date> visitedFeedMsgTitleStore =
                new BerkelyDBStore<String, Date>(dbPath,String.class,Date.class,"titleTable");

        MongoClientURI uri  = new MongoClientURI("mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db");
        MongoClient client = new MongoClient(uri);
        DB db = client.getDB(uri.getDatabase());

        loadMongoDB(wordScoresStore,"scoreTable","word","score",db);
        loadMongoDB(wordLastUpdatedDatesStore,"dateTable","word","updateDate",db);
        loadMongoDB(visitedFeedMsgTitleStore,"titleTable","title","viewDate",db);
        client.close();
    }*/

    private void loadMongoDB(BerkelyDBStore<String,?> bdbStore,
                             String tableName,
                             String keyName,
                             String valueName,
                             DB db) throws Exception {
        DBCollection collection = db.getCollection(tableName);

        List<BasicDBObject> seedData = new ArrayList<BasicDBObject>();
        for(String s: bdbStore.getKeys())
        {
            BasicDBObject obj = new BasicDBObject();
            obj.put(keyName,s);
            obj.put(valueName,bdbStore.get(s));
            seedData.add(obj);
        }
        collection.insert(seedData.toArray(new BasicDBObject[0]));
    }

}
