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
    public static final Map<String,RSSFeedDescriptor> feeds = new HashMap<String, RSSFeedDescriptor>();
    static
    {
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml",new NYTimesRSSFeedDescriptor("NYTimes Homepage"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/InternationalHome.xml",new NYTimesRSSFeedDescriptor("NYTimes International"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/World.xml",new NYTimesRSSFeedDescriptor("NYTimes World"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/US.xml",new NYTimesRSSFeedDescriptor("NYTimes US"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Business.xml",new NYTimesRSSFeedDescriptor("NYTimes Business"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Technology.xml",new NYTimesRSSFeedDescriptor("NYTimes Tech"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Science.xml",new NYTimesRSSFeedDescriptor("NYTimes Science"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Health.xml",new NYTimesRSSFeedDescriptor("NYTimes Health"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Arts.xml",new NYTimesRSSFeedDescriptor("NYTimes Arts"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/FashionandStyle.xml",new NYTimesRSSFeedDescriptor("NYTimes Style"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Travel.xml",new NYTimesRSSFeedDescriptor("NYTimes Travel"));
        feeds.put("http://6thfloor.blogs.nytimes.com/feed/",new NYTimesRSSFeedDescriptor("NYTimes Magazine"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/RealEstate.xml",new NYTimesRSSFeedDescriptor("NYTimes Real Estate"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Automobiles.xml",new NYTimesRSSFeedDescriptor("NYTimes Auto"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/AsiaPacific.xml",new NYTimesRSSFeedDescriptor("NYTimes Asian"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Europe.xml",new NYTimesRSSFeedDescriptor("NYTimes Europe"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Americas.xml",new NYTimesRSSFeedDescriptor("NYTimes Americas"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/MiddleEast.xml",new NYTimesRSSFeedDescriptor("NYTimes MidEast"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Africa.xml",new NYTimesRSSFeedDescriptor("NYTimes Africa"));
        feeds.put("http://rss.nytimes.com/services/xml/rss/nyt/Education.xml",new NYTimesRSSFeedDescriptor("NYTimes Education"));
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Politics.xml",new NYTimesRSSFeedDescriptor("NYTimes Politics"));
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Economy.xml",new NYTimesRSSFeedDescriptor("NYTimes Economy"));
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/YourMoney.xml",new NYTimesRSSFeedDescriptor("NYTimes Your Money"));
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/InternationalBusiness.xml",new NYTimesRSSFeedDescriptor("NYTimes International Business"));
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Environment.xml",new NYTimesRSSFeedDescriptor("NYTimes Environment"));
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Nutrition.xml",new NYTimesRSSFeedDescriptor("NYTimes Nutrition"));
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/HealthCarePolicy.xml",new NYTimesRSSFeedDescriptor("NYTimes Health Care Policy"));
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Books.xml",new NYTimesRSSFeedDescriptor("NYTimes Books"));
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/ArtandDesign.xml",new NYTimesRSSFeedDescriptor("NYTimes Art and Design"));
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Television.xml",new NYTimesRSSFeedDescriptor("NYTimes Television"));
        feeds.put("http://www.nytimes.com/services/xml/rss/nyt/Space.xml",new NYTimesRSSFeedDescriptor("NYTimes Space"));
        feeds.put("https://aeon.co/feed.rss",new AeonRSSFeedDescriptor("Aeon"));
    }
}
