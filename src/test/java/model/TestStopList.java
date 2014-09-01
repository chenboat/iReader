package model;

import com.intelliReader.model.StopWordFilter;
import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/20/14
 * Time: 3:12 PM
 */
public class TestStopList extends TestCase {
    public void testStopWordList()
    {
        StopWordFilter filter = new StopWordFilter();

        assertTrue(filter.isStopWord("a"));
        assertTrue(filter.isStopWord("an"));
        assertTrue(filter.isStopWord("and"));
        assertTrue(filter.isStopWord("is"));
        assertTrue(filter.isStopWord("are"));
        assertTrue(filter.isStopWord("was"));
        assertTrue(filter.isStopWord("am"));
        assertTrue(filter.isStopWord("you"));


    }
}
