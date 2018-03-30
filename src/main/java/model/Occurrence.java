package model;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.security.PrivateKey;

@Embedded
public class Occurrence {
    @Property
    private Integer urlId, tf;

    public Occurrence(Integer urlId, Integer tf) {
        this.urlId = urlId;
        this.tf = tf;
    }

    public Occurrence() {
    }
}
