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

    public Entry(String id, String value) {
        this.id = id;
        this.value = value;
    }
    // for javax.xml.bind.annotation
    public Entry() {
    }

}
