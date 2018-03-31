package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Property;

@Entity
public class URL {
    @Id
    private ObjectId _id;
    @Indexed (unique = true, background = true)
    private Integer urlId;
    @Property
    private String url;
    private Double urlRank;

    public URL(Integer urlId, String url) {
        this.urlId = urlId;
        this.url = url;
        this.urlRank = 1.0;
    }

    public URL() {
    }

    public void setUrlRank(Double urlRank) {
        this.urlRank = urlRank;
    }
}
