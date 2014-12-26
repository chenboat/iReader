package Text;

import com.intelliReader.model.Stemmer;
import com.intelliReader.model.StopWordFilter;
import com.intelliReader.text.TextAnalyzer;
import junit.framework.TestCase;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Attribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.jar.Attributes;

/**
 * User: ting
 * Date: 10/17/14
 * Time: 11:12 PM
 */
public class TestLuceneAnalyzer extends TestCase {
    public void testLuceneStandardAnalyzer() throws IOException {
        StandardTokenizer tokenizer = new StandardTokenizer(new StringReader("GM's Third-Quarter Global Vehicle Sales Up 2 Percent on Strong China, U.S. Demand"));
        tokenizer.reset();
        while(tokenizer.incrementToken()){
            System.out.println(tokenizer.getAttribute(CharTermAttribute.class));
        }
        tokenizer.end();
        tokenizer.close();
    }

    public void testIReaderAnalyzer() throws IOException {
        String[] arr = TextAnalyzer.tokenizeLowerCaseAndRemoveStopWordAndStem(
                "GM's Third-Quarter Global Vehicle Sales Up 2 Percent on Strong China, U.S. Demand",
                new StopWordFilter(),
                new Stemmer()
        );
        for(String s : arr){
            System.out.println(s);
        }
    }
}
