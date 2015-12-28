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
                    "http://rss.nytimes.com/c/34625/f/640305/s/3ed60b43/sc/39/l/0L0Snytimes0N0C20A140C0A90C260Cworld0Cafrica0Csouth0Esudan0Echina0Eto0Esend0Etroops0Efor0Eun0Emission0Bhtml0Dpartner0Frss0Gemc0Frss/story01.htm",
                    "http://rss.nytimes.com/c/34625/f/640347/s/3ec10cb6/sc/29/l/0Ldotearth0Bblogs0Bnytimes0N0C20A140C0A90C230Cnew0Eco20Eemissions0Ereport0Eshows0Echinas0Ecentral0Erole0Ein0Eshaping0Eworlds0Eclimate0Epath0C0Dpartner0Frss0Gemc0Frss/story01.htm",
                    "http://rss.nytimes.com/c/34625/f/640310/s/3eb443ed/sc/39/l/0L0Snytimes0N0C20A140C0A90C230Cworld0Casia0Chong0Ekong0Estudents0Elead0Edemocracy0Efight0Ewith0Eclass0Eboycott0Bhtml0Dpartner0Frss0Gemc0Frss/story01.htm",
                    "http://rss.nytimes.com/c/34625/f/640310/s/3eddc8cf/sc/7/l/0L0Snytimes0N0C20A140C0A90C270Cworld0Casia0Cindia0Echina0Eladakh0Edispute0Bhtml0Dpartner0Frss0Gemc0Frss/story01.htm",
                    "http://rss.nytimes.com/c/34625/f/640329/s/3ed568f2/sc/22/l/0L0Snytimes0N0C20A140C0A90C260Cbusiness0Cinternational0Cin0Echina0Esteel0Econsumption0Edrops0Eas0Eeconomy0Eslows0Bhtml0Dpartner0Frss0Gemc0Frss/story01.htm",
                    "http://rss.nytimes.com/c/34625/f/667200/s/3ee4a63d/sc/11/l/0L0Snytimes0N0C20A140C0A90C280Cworld0Cafrica0Cliberia0Ehealth0Echief0Eis0Eunder0Equarantine0Bhtml0Dpartner0Frss0Gemc0Frss/story01.htm",
                    "http://rss.nytimes.com/c/34625/f/640350/s/3ee93a60/sc/13/l/0L0Snytimes0N0C20A140C0A90C290Csports0Cgolf0Cryder0Ecup0E20A140Eeuropeans0Emake0Eeasy0Ework0Eof0Eus0Efor0Ethird0Estraight0Evictory0Bhtml0Dpartner0Frss0Gemc0Frss/story01.htm",
                    "http://rss.nytimes.com/c/34625/f/640380/s/4b3e15b4/sc/15/l/0L0Snytimes0N0Cvideo0Cscience0Cspace0C10A0A0A0A0A0A0A40A190A440Cnasa0Ereleases0Ehigh0Edefinition0Esun0Evideo0Bhtml0Dpartner0Frss0Gemc0Frss/story01.htm"
        };

        for(String s:str){
            String picUrl = HTMLUtil.getPicURLFromNYTimesLink(s);
            assert picUrl == null || !picUrl.contains("\"");
        }

    }
}
