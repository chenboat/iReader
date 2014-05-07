package test.berkeleydb;

import com.sleepycat.je.*;
import junit.framework.TestCase;

import java.io.*;


/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/21/14
 * Time: 10:26 PM
 */
public class TestBdbBasics extends TestCase {
    public void testOpenDB()
    {
        String projRoot = System.getProperty("user.dir");
        String dbPath = projRoot + "/src/test/resources/dbEnv";

        Environment myDbEnvironment = null;
        Database myDatabase = null;

        try {
            // Open the environment. Create it if it does not already exist.
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setAllowCreate(true);
            myDbEnvironment = new Environment(new File(dbPath),envConfig);

            // Open the database. Create it if it does not already exist.
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            myDatabase = myDbEnvironment.openDatabase(null,
                    "sampleDatabase",
                    dbConfig);

            String aKey = "myFirstKey";
            String aData = "myFirstData";

            DatabaseEntry theKey = new DatabaseEntry(aKey.getBytes("UTF-8"));
            DatabaseEntry theData = new DatabaseEntry(aData.getBytes("UTF-8"));
            myDatabase.put(null, theKey, theData);

            DatabaseEntry retrieved = new DatabaseEntry();

            // Perform the get.
            if (myDatabase.get(null, theKey, retrieved, LockMode.DEFAULT) ==
                    OperationStatus.SUCCESS) {

                // Recreate the data String.
                byte[] retData = theData.getData();
                String foundData = new String(retData, "UTF-8");
                System.out.println("For key: '" + aKey + "' found data: '" +
                        foundData + "'.");
            } else {
                System.out.println("No record found for key '" + aKey + "'.");
            }

        } catch (DatabaseException dbe) {
            // Exception handling goes here
            dbe.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (myDatabase != null) {
                myDatabase.close();
            }

            if (myDbEnvironment != null) {
                myDbEnvironment.close();
            }
        }
    }

}
