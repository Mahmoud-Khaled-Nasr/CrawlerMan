package model;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

@Embedded
public class Occurrence {
    @Property
    private Integer urlId, count;

    public Occurrence(Integer urlId, Integer count) {
        this.urlId = urlId;
        this.count = count;
    }

    public Occurrence() {
    }

    public Integer getUrlId() {
        return urlId;
    }

    public Integer getCount(){
        return count;
    }
}