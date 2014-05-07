package com.intelliReader.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/26/14
 * Time: 3:10 PM
 */
public class InMemoryStore<K,V> implements Store<K,V>{

    private Map<K,V> map;

    public InMemoryStore()
    {
        map = new HashMap<K,V>();
    }
    @Override
    public V get(K key) throws Exception {
        return map.get(key);
    }

    @Override
    public void put(K key, V value) throws Exception {
        map.put(key,value);
    }

    @Override
    public Set<K> getKeys() throws Exception {
        return map.keySet();
    }

    @Override
    public void sync() {
        // do nothing
    }
}
