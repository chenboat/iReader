package com.intelliReader.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import com.intelliReader.storage.Store;


/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/20/14
 * Time: 2:09 PM
 */
public class StopWordFilter {
    private final Set<String> stopWords = new HashSet<String>();
    private Logger log = Logger.getLogger(StopWordFilter.class.getName());

    public StopWordFilter(Store<String,Date> store)
    {
        try {
            for(String s: store.getKeys())
            {
                stopWords.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Total number of stop words:" + stopWords.size());
    }

    /**
     *
     * @param store
     * @param userId the userId to filter out keywords not by an user
     */
    public StopWordFilter(Store<String,Date> store,String userId)
    {
        try {
            for(String s: store.getKeys())
            {
                if(s.startsWith(userId)) {
                    stopWords.add(s.substring(userId.length() + 1)); // 1 for delimiter :
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Total number of stop words:" + stopWords.size());
    }


    public boolean isStopWord(String s)
    {
        return stopWords.contains(s);
    }

}
