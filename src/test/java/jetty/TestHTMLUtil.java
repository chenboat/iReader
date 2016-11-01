package jetty;

import com.intelliReader.jetty.HTMLUtil;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: ting
 * Date: 9/27/14
 * Time: 11:36 AM
 */
public class TestHTMLUtil extends TestCase {

    public void testURLReader() throws IOException {
        String[] str = {
          "http://www.nytimes.com/2016/04/15/technology/why-do-older-people-love-facebook-lets-ask-my-dad.html?partner=rss"
        };

        for(String s:str){
            String picUrl = HTMLUtil.getPicURLFromNYTimesLink(s);
            System.out.println("picUrl:" + picUrl);
            assert picUrl == null || !picUrl.contains("\"");
        }

    }
}
