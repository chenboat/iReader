package model;

import com.intelliReader.model.KeywordBasedFeedRelevanceModel;
import com.intelliReader.model.Stemmer;
import com.intelliReader.model.StopWordFilter;
import com.intelliReader.newsfeed.FeedMessage;
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
        FeedMessage f1 = new FeedMessage("stock market"," economic news");
        FeedMessage f2 = new FeedMessage("sports nba"," nba results");
        FeedMessage f3 = new FeedMessage("wall street "," flash boy stock");

        KeywordBasedFeedRelevanceModel model = new KeywordBasedFeedRelevanceModel(
                                                                    new InMemoryStore<String, Double>(),
                                                                    new InMemoryStore<String, Date>(),
                                                                    new StopWordFilter(new InMemoryStore<String, Date>()),
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
        FeedMessage f1 = new FeedMessage("stock market"," economic news");
        FeedMessage f2 = new FeedMessage("sports nba"," nba results");
        FeedMessage f3 = new FeedMessage("wall street "," flash boy stock");

        KeywordBasedFeedRelevanceModel model = new KeywordBasedFeedRelevanceModel(
                                            new InMemoryStore<String, Double>(),
                                            new InMemoryStore<String, Date>(),
                                            new StopWordFilter(new InMemoryStore<String, Date>()),new Stemmer());

        Calendar calendar = Calendar.getInstance();

        calendar.set(104,4,20);
        Date april20th = calendar.getTime();
        model.addFeed(f1, april20th);
        model.addFeed(f2, april20th);

        calendar.set(104,4,21);
        Date april21th = calendar.getTime();
        model.addFeed(f3, april21th);



        FeedMessage t1 = new FeedMessage("china digest"," ");
        FeedMessage t2 = new FeedMessage("nba latest"," ");
        FeedMessage t3 = new FeedMessage("stock overview"," ");

        List<FeedMessage> lst = new ArrayList<FeedMessage>();
        lst.add(t1);
        lst.add(t2);
        lst.add(t3);

        List<KeywordBasedFeedRelevanceModel.ScoredFeedMessage> rankedLst = model.rankFeeds(lst, april21th);

        assertEquals("stock overview",rankedLst.get(0).getMsg().getTitle());
        assertEquals("nba latest",rankedLst.get(1).getMsg().getTitle());
        assertEquals("china digest",rankedLst.get(2).getMsg().getTitle());

    }

    public void testAddFeedsAndRankingUsingBDBStore()
    {
        FeedMessage f1 = new FeedMessage("stock market"," economic news");
        FeedMessage f2 = new FeedMessage("sports nba"," nba results");
        FeedMessage f3 = new FeedMessage("wall street "," flash boy stock");

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
                new StopWordFilter(new InMemoryStore<String, Date>()),
                new Stemmer());

        Calendar calendar = Calendar.getInstance();

        calendar.set(104,4,20);
        Date april20th = calendar.getTime();
        model.addFeed(f1, april20th);
        model.addFeed(f2, april20th);

        calendar.set(104,4,21);
        Date april21th = calendar.getTime();
        model.addFeed(f3, april21th);


        FeedMessage t1 = new FeedMessage("china digest"," ");
        FeedMessage t2 = new FeedMessage("nba latest"," ");
        FeedMessage t3 = new FeedMessage("stock overview"," ");

        List<FeedMessage> lst = new ArrayList<FeedMessage>();
        lst.add(t1);
        lst.add(t2);
        lst.add(t3);

        List<KeywordBasedFeedRelevanceModel.ScoredFeedMessage> rankedLst = model.rankFeeds(lst, april21th);

        assertEquals("stock overview",rankedLst.get(0).getMsg().getTitle());
        assertEquals("nba latest",rankedLst.get(1).getMsg().getTitle());
        assertEquals("china digest",rankedLst.get(2).getMsg().getTitle());
    }

}
