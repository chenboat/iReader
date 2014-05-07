package test.model;

import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.model.Stemmer;
import com.intelliReader.model.StopWordFilter;
import com.intelliReader.newsfeed.Feed;
import com.intelliReader.storage.BerkelyDBStore;
import com.intelliReader.storage.InMemoryStore;
import com.intelliReader.storage.Store;
import junit.framework.TestCase;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/19/14
 * Time: 4:21 PM
 */
public class TestKeywordBasedFeedRelevanceModel extends TestCase {

    public void testAddFeedsUsingInMemoryStore()
    {
        Feed f1 = new Feed("stock market",null," economic news",null,null,null);
        Feed f2 = new Feed("sports nba",null," nba results",null,null,null);
        Feed f3 = new Feed("wall street Is good ",null," flash boy stock",null,null,null);

        KeywordBasedFeedRelevanceModel model = new KeywordBasedFeedRelevanceModel(
                                                                    new InMemoryStore<String, Double>(),
                                                                    new InMemoryStore<String, Date>(),
                                                                    new StopWordFilter(),
                                                                    new Stemmer());

        Calendar calendar = Calendar.getInstance();

        calendar.set(104,4,20);
        Date april20th = calendar.getTime();
        model.addFeed(f1, april20th);
        model.addFeed(f2, april20th);

        calendar.set(104,4,21);
        Date april21th = calendar.getTime();
        model.addFeed(f3, april21th);

        Store<String,Double> wordScores = model.getWordScores();
        Store<String,Date> wordDate = model.getWordLastUpdatedDates();

        try{
            assertEquals(1.5,wordScores.get("stock")); // 1 + 1 *0.5
            assertEquals(april21th,wordDate.get("stock"));

            assertEquals(2.0,wordScores.get("nba")); // (1 + 1) = 2
            assertEquals(april20th,wordDate.get("nba"));

            assertEquals(1.0,wordScores.get("street")); // 1 = 1
            assertEquals(april21th,wordDate.get("street"));

            assertTrue(wordScores.get("is") == null);
            assertTrue(wordScores.get("Is") == null);
        }catch(Exception e)
        {
            assertTrue(false);
        }
    }

    public void testRanking()
    {
        Feed f1 = new Feed("stock market",null," economic news",null,null,null);
        Feed f2 = new Feed("sports nba",null," nba results",null,null,null);
        Feed f3 = new Feed("wall street ",null," flash boy stock",null,null,null);

        KeywordBasedFeedRelevanceModel model = new KeywordBasedFeedRelevanceModel(
                                            new InMemoryStore<String, Double>(),
                                            new InMemoryStore<String, Date>(),
                                            new StopWordFilter(),new Stemmer());

        Calendar calendar = Calendar.getInstance();

        calendar.set(104,4,20);
        Date april20th = calendar.getTime();
        model.addFeed(f1, april20th);
        model.addFeed(f2, april20th);

        calendar.set(104,4,21);
        Date april21th = calendar.getTime();
        model.addFeed(f3, april21th);


        Feed t1 = new Feed("china digest",null," ",null,null,null);
        Feed t2 = new Feed("nba latest",null," ",null,null,null);
        Feed t3 = new Feed("stock overview",null," ",null,null,null);

        List<Feed> lst = new ArrayList<Feed>();
        lst.add(t1);
        lst.add(t2);
        lst.add(t3);

        List<Feed> rankedLst = model.rankFeeds(lst,april21th);

        assertEquals("stock overview",rankedLst.get(2).getTitle());
        assertEquals("nba latest",rankedLst.get(1).getTitle());
        assertEquals("china digest",rankedLst.get(0).getTitle());
    }

    public void testAddFeedsAndRankingUsingBDBStore()
    {
        Feed f1 = new Feed("stock market",null," economic news",null,null,null);
        Feed f2 = new Feed("sports nba",null," nba results",null,null,null);
        Feed f3 = new Feed("wall street ",null," flash boy stock",null,null,null);

        String projRoot = System.getProperty("user.dir");
        String dbPath = projRoot + "/src/test/resources/dbEnv";

        // Clean the directory
        File dir = new File(dbPath);
        if(dir.listFiles() != null)
        {
            for(File f: dir.listFiles())
            {
                f.delete();
            }
        }

        BerkelyDBStore<String, Double> wordScoresStore = new BerkelyDBStore<String, Double>(dbPath, String.class, Double.class, "scoreTable" );
        BerkelyDBStore<String, Date> wordLastUpdatedDatesStore = new BerkelyDBStore<String, Date>(dbPath, String.class, Date.class, "dateTable");
        KeywordBasedFeedRelevanceModel model = new KeywordBasedFeedRelevanceModel(
                wordScoresStore,
                wordLastUpdatedDatesStore,
                new StopWordFilter(),
                new Stemmer());

        Calendar calendar = Calendar.getInstance();

        calendar.set(104,4,20);
        Date april20th = calendar.getTime();
        model.addFeed(f1, april20th);
        model.addFeed(f2, april20th);

        calendar.set(104,4,21);
        Date april21th = calendar.getTime();
        model.addFeed(f3, april21th);


        Feed t1 = new Feed("china digest",null," ",null,null,null);
        Feed t2 = new Feed("nba latest",null," ",null,null,null);
        Feed t3 = new Feed("stock overview",null," ",null,null,null);

        List<Feed> lst = new ArrayList<Feed>();
        lst.add(t1);
        lst.add(t2);
        lst.add(t3);

        List<Feed> rankedLst = model.rankFeeds(lst,april21th);

        assertEquals("stock overview",rankedLst.get(2).getTitle());
        assertEquals("nba latest",rankedLst.get(1).getTitle());
        assertEquals("china digest",rankedLst.get(0).getTitle());
    }

}
