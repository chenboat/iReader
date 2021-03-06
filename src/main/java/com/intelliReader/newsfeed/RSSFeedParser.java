package com.intelliReader.newsfeed;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/13/14
 * Time: 10:58 PM
 */
public class RSSFeedParser {
    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String LANGUAGE = "language";
    static final String COPYRIGHT = "copyright";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String GUID = "guid";
    Logger log = Logger.getLogger(RSSFeedParser.class.getName());

    final URL url;

    public RSSFeedParser(String feedUrl) {
        try {
            this.url = new URL(feedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Feed readFeed() throws XMLStreamException {
        Feed feed = null;
        try {
            boolean isFeedHeader = true;
            // Set header values initial to the empty string
            String description = "";
            String title = "";
            String link = "";
            String language = "";
            String copyright = "";
            String author = "";
            String pubdate = "";
            String guid = "";

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = read();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the XML document
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName()
                            .getLocalPart();
                    String prefix = event.asStartElement().getName().getPrefix();
                    if (localPart.equals(ITEM)) {
                        if (isFeedHeader) {
                            isFeedHeader = false;
                            feed = new Feed(title, link, description, language,
                                    copyright, pubdate);
                        }
                        eventReader.nextEvent();
                    } else if (localPart.equals(TITLE)) {
                        title = getCharacterData(event, eventReader);
                    } else if (localPart.equals(DESCRIPTION)) {
                        description = getCharacterData(event, eventReader);
                    } else if (localPart.equals(LINK) && prefix.isEmpty()) {
                        link = getCharacterData(event, eventReader);
                    } else if (localPart.equals(GUID)) {
                        guid = getCharacterData(event, eventReader);
                    } else if (localPart.equals(LANGUAGE)) {
                        language = getCharacterData(event, eventReader);
                    } else if (localPart.equals(AUTHOR)) {
                        author = getCharacterData(event, eventReader);
                    } else if (localPart.equals(PUB_DATE)) {
                        pubdate = getCharacterData(event, eventReader);
                    } else if (localPart.equals(COPYRIGHT)) {
                        copyright = getCharacterData(event, eventReader);
                    }
                } else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getLocalPart().equals(ITEM)) {
                        FeedMessage message = new FeedMessage();
                        message.setAuthor(author);
                        message.setDescription(description);
                        message.setGuid(guid);
                        message.setLink(link);
                        message.setTitle(title);
                        feed.getMessages().add(message);
                        eventReader.nextEvent();
                    }
                }
            }
        } catch (XMLStreamException e) {
            log.warning("Reading the RSS failed for URL:" + url);
            throw e;
        }
        return feed;
    }

    private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
            throws XMLStreamException {
        String result = "";
        while(true) {
            event = eventReader.nextEvent();
            if (event instanceof Characters) {
                result += event.asCharacters().getData();
            } else {
                break;
            }
        }
        return result;
    }

    private InputStream read() {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
