package model;

import junit.framework.TestCase;
import com.intelliReader.model.Stemmer;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/20/14
 * Time: 2:48 PM
 */
public class TestStemmer extends TestCase {

    public void testPortStemmer()
    {
        Stemmer stemmer = new Stemmer();

        String s1 = "tested";
        stemmer.add(s1.toCharArray(),s1.length());
        stemmer.stem();
        assertEquals("test",stemmer.toString());

        String s2 = "jobs";
        stemmer.add(s2.toCharArray(),s2.length());
        stemmer.stem();
        assertEquals("job",stemmer.toString());

        String s3 = "waiting";
        stemmer.add(s3.toCharArray(),s3.length());
        stemmer.stem();
        assertEquals("wait",stemmer.toString());

        String s4 = "happier";
        stemmer.add(s4.toCharArray(),s4.length());
        stemmer.stem();
        assertEquals("happier",stemmer.toString());

        String s5 = "happiness";
        stemmer.add(s5.toCharArray(),s5.length());
        stemmer.stem();
        assertEquals("happi",stemmer.toString());

        String s6 = "strangely";
        stemmer.add(s6.toCharArray(),s6.length());
        stemmer.stem();
        assertEquals("strang",stemmer.toString());

        String s7 = "strange";
        stemmer.add(s7.toCharArray(),s7.length());
        stemmer.stem();
        assertEquals("strang",stemmer.toString());

    }
}
