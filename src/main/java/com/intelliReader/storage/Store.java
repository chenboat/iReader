package com.intelliReader.storage;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/22/14
 * Time: 11:03 PM
 */
public interface Store<K,V> {
    // Get the value of a given key, null if it does not exist
    public V get(K key) throws Exception;
    // Insert the value with a given key, overwrite the old value if the key is already in the store
    public void put(K key, V value) throws Exception;
    // Return all the keys in the store
    public Set<K> getKeys() throws Exception;
    // Persist the data in the store to disk
    public void sync();
    // Remove the key,value pair
    public void delete(K key) throws Exception;
}
