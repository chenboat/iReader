package test.berkeleydb;

import com.intelliReader.storage.BerkelyDBStore;
import junit.framework.TestCase;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/23/14
 * Time: 11:13 PM
 */
public class TestBerkeleyDBStore extends TestCase {
    public void testBasics() {
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

        BerkelyDBStore<String,Date> bdbStore = new BerkelyDBStore<String,Date>
                                                (dbPath,String.class, Date.class, "sampleTable");

        Calendar calendar = Calendar.getInstance();

        calendar.set(104,4,20);
        Date april20th = calendar.getTime();

        calendar.set(104,4,21);
        Date april21st = calendar.getTime();

        try{
            bdbStore.put("good",april20th);
            Date d1 = bdbStore.get("good");

            assertEquals(april20th,d1);

            bdbStore.put("worse",april21st);
            Date d2 = bdbStore.get("worse");

            assertEquals(april21st,d2);

            Set<String> keys = bdbStore.getKeys();
            assertEquals(2,keys.size());
            assertTrue(keys.contains("good"));
            assertTrue(keys.contains("worse"));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        bdbStore.close();
    }
}
