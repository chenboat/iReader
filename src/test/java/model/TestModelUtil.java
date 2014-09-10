package model;

import com.intelliReader.model.ModelUtil;
import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 9/9/14
 * Time: 3:44 PM
 */
public class TestModelUtil extends TestCase {
    public void testExponentialDecayScore(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(104,9,1);
        Date prev = calendar.getTime();

        calendar.set(104,9,3);
        Date now = calendar.getTime();

        assertEquals(0.05,ModelUtil.exponentialDecayScore(0.2,prev,now));
    }
}
