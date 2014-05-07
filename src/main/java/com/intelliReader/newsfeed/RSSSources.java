package com.intelliReader.newsfeed;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 5/3/14
 * Time: 12:51 PM
 */
public class RSSSources {
    public static final Map<String,String> feeds = new HashMap<String, String>();
    static
    {
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml","NYTimes Homepage");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/InternationalHome.xml","NYTimes International");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/World.xml","NYTimes World");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/US.xml","NYTimes US");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Business.xml","NYTimes Business");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Technology.xml","NYTimes Tech");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Science.xml","NYTtimes Science");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Health.xml","NYTtimes Health");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Arts.xml","NYTtimes Arts");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/FashionandStyle.xml","NYTtimes Style");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Travel.xml","NYTtimes Travel");
        feeds.put("http://6thfloor.blogs.nytimes.com/feed/","NYTtimes Magazine");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/RealEstate.xml","NYTtimes Real Estate");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Automobiles.xml","NYTtimes Auto");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/AsiaPacific.xml","NYTtimes Asian");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Europe.xml","NYTtimes Europe");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Americas.xml","NYTtimes Americas");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/MiddleEast.xml","NYTtimes MidEast");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Africa.xml","NYTtimes Africa");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Education.xml","NYTtimes Education");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Politics.xml","NYTtimes Politics");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Economy.xml","NYTtimes Economy");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/YourMoney.xml","NYTtimes Your Money");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/InternationalBusiness.xml","NYTtimes International Business");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Environment.xml","NYTtimes Environment");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Nutrition.xml","NYTtimes Nutrition");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/HealthCarePolicy.xml","NYTtimes Health Care Policy");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Books.xml","NYTtimes Books");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/ArtandDesign.xml","NYTtimes Art and Design");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Television.xml","NYTtimes Television");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Space.xml","NYTtimes Space");

    }
}
