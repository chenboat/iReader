package com.intelliReader.newsfeed;

/**
 * Created with IntelliJ IDEA.
 * Author: Ting Chen
 * Date: 4/13/14
 * Time: 10:56 PM
 *
 * A class models a web page link sent by a RSS. It represents a new web page.
 */
public class FeedMessage {
    String title;
    String description;
    String link;
    String author;
    String guid;

    public FeedMessage() {
        //To change body of created methods use File | Settings | File Templates.
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        // Remove all image src from the text description.
        this.description = description.replaceAll("<img src=[^>]*>", "");
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public FeedMessage(String t, String d){
        title = t;
        description = d;
    }


    @Override
    public String toString() {
        return "FeedMessage [title=" + title + ", description=" + description
                + ", link=" + link + ", author=" + author + ", guid=" + guid
                + "]";
    }
}
