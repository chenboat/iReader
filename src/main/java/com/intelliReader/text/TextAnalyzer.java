package com.intelliReader.text;

import com.intelliReader.model.Stemmer;
import com.intelliReader.model.StopWordFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

/**
 * User: ting
 * Date: 10/18/14
 * Time: 10:36 PM
 */
public class TextAnalyzer {
    static String[] EMPTY_STRING = new String[0];
    public static String[] tokenizeLowerCaseAndRemoveStopWordAndStem(String s,
                                                                     StopWordFilter stopWordFilter,
                                                                     Stemmer stemmer){
        if(s == null)
        {
            return EMPTY_STRING;
        }
        else
        {
            try{
                Vector<String> strV = new Vector<String>();
                StandardTokenizer tokenizer = new StandardTokenizer(new StringReader(s));
                tokenizer.reset();
                while(tokenizer.incrementToken()){         // tokenize the stream
                    String str = tokenizer.getAttribute(CharTermAttribute.class).toString();
                    str = str.toLowerCase(); // lower case
                    if(!stopWordFilter.isStopWord(str)){   //remove stop word
                        strV.add(stem(str,stemmer));         //stem
                    }
                }
                tokenizer.end();
                tokenizer.close();

                String[] arr = new String[strV.size()];
                return strV.toArray(arr);
            }catch (IOException e){
                return EMPTY_STRING;
            }
            //return s.split("\\W");
        }
    }

    public static String stem(String word, Stemmer stemmer){
        stemmer.add(word.toCharArray(),word.length());
        stemmer.stem();
        return stemmer.toString();
    }
}
