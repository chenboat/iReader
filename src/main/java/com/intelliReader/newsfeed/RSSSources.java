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
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Science.xml","NYTimes Science");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Health.xml","NYTimes Health");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Arts.xml","NYTimes Arts");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/FashionandStyle.xml","NYTimes Style");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Travel.xml","NYTimes Travel");
        feeds.put("http://6thfloor.blogs.nytimes.com/feed/","NYTimes Magazine");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/RealEstate.xml","NYTimes Real Estate");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Automobiles.xml","NYTimes Auto");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/AsiaPacific.xml","NYTimes Asian");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Europe.xml","NYTimes Europe");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Americas.xml","NYTimes Americas");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/MiddleEast.xml","NYTimes MidEast");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Africa.xml","NYTimes Africa");
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Education.xml","NYTimes Education");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Politics.xml","NYTimes Politics");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Economy.xml","NYTimes Economy");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/YourMoney.xml","NYTimes Your Money");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/InternationalBusiness.xml","NYTimes International Business");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Environment.xml","NYTimes Environment");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Nutrition.xml","NYTimes Nutrition");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/HealthCarePolicy.xml","NYTimes Health Care Policy");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Books.xml","NYTimes Books");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/ArtandDesign.xml","NYTimes Art and Design");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Television.xml","NYTimes Television");
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Space.xml","NYTimes Space");

    }
}
