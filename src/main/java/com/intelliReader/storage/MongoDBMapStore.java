package com.intelliReader.storage;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * User: ting
 * Date: 4/19/2015
 * Time: 3:00 PM
 */
public class MongoDBMapStore<K> implements Store<K,java.util.Map> {
    private String uri; // mongo db connectin uri
    private String keyName; // the field name of the key
    private String collection;
    private DBCollection dbCollection;
    public MongoDBMapStore(String dbUri, String collection, String kName) throws UnknownHostException {
        this.uri = dbUri;
        this.keyName = kName;
        this.collection = collection;
        init();
    }

    private void init() throws UnknownHostException {
        MongoClientURI mUri = new MongoClientURI(uri);
        MongoClient client = new MongoClient(mUri);
        DB db = client.getDB(mUri.getDatabase());
        dbCollection = db.getCollection(collection);
    }

    public String getKeyName() {
        return keyName;
    }

    @Override
    public java.util.Map get(K key) throws Exception {
        BasicDBObject query = new BasicDBObject(keyName,key);
        DBCursor cursor = dbCollection.find(query);
        if(cursor.hasNext()){
            return cursor.next().toMap();
        }
        cursor.close();
        return null;
    }

    @Override
    public void put(K key, java.util.Map value) throws Exception {
        BasicDBObject query = new BasicDBObject(keyName,key);
        dbCollection.remove(query);
        BasicDBObject doc = new BasicDBObject(keyName,key);
        doc.putAll(value);
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
    public java.util.Map<K,java.util.Map> getAll() throws Exception {
        DBCursor cursor = dbCollection.find();
        java.util.Map<K, java.util.Map> result = new HashMap<K, java.util.Map>();
        try{
            while(cursor.hasNext()){
                DBObject obj = cursor.next();
                result.put((K)obj.get(keyName),  obj.toMap());
            }
        }finally {
            cursor.close();
        }
        return result;
    }
}
