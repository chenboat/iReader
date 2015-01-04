package com.intelliReader.storage;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * User: ting
 * Date: 1/2/2015
 * Time: 11:34 AM
 */
public class MongoDBStore<K,V> implements Store<K, V> {
    private String uri; // mongo db connectin uri
    private String keyName; // the field name of the key
    private String valueName; // the field name of the value
    private String collection;
    private DBCollection dbCollection;
    public MongoDBStore(String dbUri, String collection, String kName,String vName) throws UnknownHostException {
        this.uri = dbUri;
        this.keyName = kName;
        this.valueName = vName;
        this.collection = collection;
        init();
    }

    private void init() throws UnknownHostException {
        MongoClientURI mUri = new MongoClientURI(uri);
        MongoClient client = new MongoClient(mUri);
        DB db = client.getDB(mUri.getDatabase());
        dbCollection = db.getCollection(collection);
    }


    @Override
    public V get(K key) throws Exception {
        BasicDBObject query = new BasicDBObject(keyName,key);
        DBCursor cursor = dbCollection.find(query);
        if(cursor.hasNext()){
            return (V)cursor.next().get(valueName);
        }
        cursor.close();
        return null;
    }

    @Override
    public void put(K key, V value) throws Exception {
        BasicDBObject query = new BasicDBObject(keyName,key);
        dbCollection.remove(query);
        BasicDBObject doc = new BasicDBObject(keyName,key).append(valueName,value);
        dbCollection.insert(doc);
    }

    @Override
    public Set<K> getKeys() throws Exception {
        DBCursor cursor = dbCollection.find();
        Set<K> result = new TreeSet<K>();
        try{
            while(cursor.hasNext()){
                result.add((K)cursor.next().get(keyName));
            }
        }finally {
            cursor.close();
        }
        return result;
    }

    @Override
    public void sync() {

    }

    @Override
    public void delete(K key) throws Exception {
        BasicDBObject query = new BasicDBObject(keyName,key);
        dbCollection.remove(query);
    }

    @Override
    public Map<K, V> getAll() throws Exception {
        DBCursor cursor = dbCollection.find();
        Map<K,V> result = new HashMap<K, V>();
        try{
            while(cursor.hasNext()){
                DBObject obj = cursor.next();
                result.put((K)obj.get(keyName),(V)obj.get(valueName));
            }
        }finally {
            cursor.close();
        }
        return result;
    }
}
