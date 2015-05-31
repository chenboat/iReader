package com.intelliReader.jetty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.Serializable;
import java.util.Map;

@XmlRootElement
/**
 * User: ting
 * Date: 5/31/2015
 * Time: 12:20 PM
 */

public class MapJson<K,V> {
    @XmlElement
    public Map<K,V> getMap() {
        return map;
    }

    public void setMap(Map<K,V> map) {
        this.map = map;
    }

    Map<K,V> map;
}
