package com.intelliReader.jetty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class Entry implements Serializable {
    @XmlElement
    public String getId() {
        return id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    @XmlElement
    private String value;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public Entry(String id, String value, String userId) {
        this.id = id;
        this.value = value;
        this.userId = userId;
    }
    // for javax.xml.bind.annotation
    public Entry() {
    }

}
