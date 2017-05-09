package com.intelliReader.storage;

import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MongoDBMapStoreTest {

    @Test
    public void testGet() throws Exception {
        String dbUri = "mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db";
        MongoDBMapStore<String> store = new MongoDBMapStore<String>(dbUri,"testStore","key");

        Map<String,String> m1 = new HashMap<String, String>();
        m1.put("k1","v1");
        m1.put("k2","v2");


        store.put("key1",m1);
        System.out.println(store.get("key1"));
    }

    @Test
    public void testPut() throws Exception {
        String dbUri = "mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db";
        MongoDBMapStore<String> store = new MongoDBMapStore<String>(dbUri,"testStore","key");

        Map<String,String> m1 = new HashMap<String, String>();
        m1.put("k1","v1");
        m1.put("k2","v2");

        Map<String,String> m2 = new HashMap<String, String>();
        m2.put("k1","v3");
        m2.put("k2","v4");

        store.put("key1",m1);
        store.put("key2",m2);
    }

    @Test
    public void testGetKeys() throws Exception {


    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testGetAll() throws Exception {

    }
}