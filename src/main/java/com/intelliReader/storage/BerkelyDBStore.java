package com.intelliReader.storage;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.*;
import com.sleepycat.je.*;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/22/14
 * Time: 11:07 PM
 */
public class BerkelyDBStore<K extends Serializable,V extends Serializable> implements Store<K,V> {
    Environment myDbEnvironment = null;
    Database myDatabase = null;
    private final EntryBinding keyBinding;
    private final EntryBinding valueBinding;

    public BerkelyDBStore(String dbPath, Class keyClass, Class valueClass, String dbName)
    {
        dbinit(dbPath,dbName);
        this.keyBinding = getBinding(keyClass);
        this.valueBinding = getBinding(valueClass);
    }

    private EntryBinding getBinding(Class type) {
        if(type == String.class)
        {
            return new StringBinding();
        }else if(type == Character.class)
        {
            return new CharacterBinding();
        }else if(type == Boolean.class)
        {
            return new BooleanBinding();
        }else if(type == Byte.class)
        {
            return new ByteBinding();
        }else if(type == Short.class)
        {
            return new ShortBinding();
        }else if(type == Integer.class)
        {
            return new IntegerBinding();
        }else if(type == Long.class)
        {
            return new LongBinding();
        }else if(type == Float.class)
        {
            return new FloatBinding();
        }else if(type == Double.class)
        {
            return new DoubleBinding();
        }else
        {
            return new SerialBinding(new StoredClassCatalog(myDatabase),type);
        }
        }

    private void dbinit(String dbPath, String dbName) {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        myDbEnvironment = new Environment(new File(dbPath),envConfig);

        // Open the database. Create it if it does not already exist.
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setDeferredWrite(true);
        myDatabase = myDbEnvironment.openDatabase(null,dbName,dbConfig);
    }

    @Override
    public V get(K key) throws UnsupportedEncodingException {
        DatabaseEntry theKey = new DatabaseEntry();
        keyBinding.objectToEntry(key,theKey);
        DatabaseEntry retrieved = new DatabaseEntry();
        // Perform the get.
        if (myDatabase.get(null, theKey, retrieved, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
            // Recreate the data String.
            return (V)valueBinding.entryToObject(retrieved);
        } else {
            return null;
        }
    }

    @Override
    public void put(K key, V value) throws UnsupportedEncodingException {
        DatabaseEntry theKey = new DatabaseEntry();
        keyBinding.objectToEntry(key,theKey);
        DatabaseEntry theData = new DatabaseEntry();
        valueBinding.objectToEntry(value, theData);
        // Now store it
        myDatabase.put(null, theKey, theData);
    }

    @Override
    public Set<K> getKeys() throws Exception {
        Cursor cursor = null;
        Set<K> keys = new HashSet<K>();
        try {
            cursor = myDatabase.openCursor(null, null);
            // Cursors need a pair of DatabaseEntry objects to operate. These hold
            // the key and data found at any given position in the database.
            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();
            // To iterate, just call getNext() until the last database record has been
            // read. All cursor operations return an OperationStatus, so just read
            // until we no longer see OperationStatus.SUCCESS
            while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) ==
                    OperationStatus.SUCCESS) {
                // getData() on the DatabaseEntry objects returns the byte array
                // held by that object. We use this to get a String value. If the
                // DatabaseEntry held a byte array representation of some other data
                // type (such as a complex object) then this operation would look
                // considerably different.
                try{
                    //TODO: this is really a hack need to know why
                    if(foundKey.getData().length > 1)
                    {
                        keys.add((K) keyBinding.entryToObject(foundKey));
                    }
                }catch (Exception e)
                {
                    //Cowardly swallow
                }
            }
        } catch (DatabaseException de) {
            throw de;
        } finally {
            // Cursors must be closed.
            cursor.close();
        }
        return keys;
    }

    @Override
    public void sync() {
        myDatabase.sync();
    }


    public void close()
    {
        myDatabase.close();
    }

}
