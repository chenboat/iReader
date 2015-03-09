package mangodb;

import com.intelliReader.storage.MongoDBStore;
import junit.framework.TestCase;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * User: ting
 * Date: 1/2/2015
 * Time: 12:16 PM
 */
public class TestMongoDBStore extends TestCase {
    public void testMongoDBStoreBasics() throws Exception {
        String dbUri = "mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db";
        MongoDBStore<String,Date> store = new MongoDBStore<String, Date>(dbUri,"test","key","value");

        Calendar calendar = Calendar.getInstance();
        calendar.set(104,4,20);
        Date april20th = calendar.getTime();
        calendar.set(104,4,21);
        Date april21st = calendar.getTime();
        // 1. put
        store.put("ting",april20th);
        store.put("chen",april21st);
        // 2. get all keys
        Set<String> keys = store.getKeys();
        assert keys.size() == 2;
        assert keys.contains("ting");
        assert keys.contains("chen");
        // 3. get all pairs
        Map<String,Date> m = store.getAll();
        assert m.size() == 2;
        assert m.get("ting").compareTo(april20th) == 0;
        assert m.get("chen").compareTo(april21st) == 0;

        // 4. get
        Date v = store.get("ting");
        assert v.compareTo(april20th) == 0;

        // 5. delete
        store.delete("ting");
        keys = store.getKeys();
        assert keys.size() == 1;
        assert keys.contains("chen");
    }
/*
    public void testOneTimeMigration() throws Exception {
        String dbUri = "mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db";
        MongoDBStore<String, Date> dateTable =
                new MongoDBStore<String, Date>(dbUri, "dateTable", "word", "updateDate");

        Map<String,Date> dates = dateTable.getAll();

        for(String k: dates.keySet()){
            if(!k.contains(":")){
                dateTable.put("boat@ting.com:"+k,dates.get(k));
            }
        }
    }
 */
}
