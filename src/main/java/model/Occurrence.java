package model;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.security.PrivateKey;

@Embedded
public class Occurrence {
    @Property
    private Integer urlId, TF;

    public Occurrence(Integer urlId, Integer TF) {
        this.urlId = urlId;
        this.TF = TF;
    }
}
