package com.intelliReader.util;

/**
 * User: ting
 * Date: 3/1/2015
 * Time: 3:25 PM
 */
public class StringUtil {
    public static String makeSSTableKey(String a, String b){
        if(a == null || a.length() == 0)
            return b;
        return a + ":" + b;
    }
}
