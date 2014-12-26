package com.intelliReader.model;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.intelliReader.jetty.StopwordResource;


/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/20/14
 * Time: 2:09 PM
 */
public class StopWordFilter {
   private final Set<String> stopWords = new HashSet<String>();
    private Logger log = Logger.getLogger(StopWordFilter.class.getName());

   public StopWordFilter()
   {
       try {
           for(String s: StopwordResource.bdbStore.getKeys())
           {
              stopWords.add(s);
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
