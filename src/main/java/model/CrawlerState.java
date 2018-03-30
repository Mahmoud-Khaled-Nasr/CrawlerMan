package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

@Entity
public class CrawlerState {

    public enum State {PENDING, VISITED };
    @Id
    private ObjectId _id;
    @Property
    private String url;
    private State urlState;

    public CrawlerState(String url, State urlState){
        this.url = url;
        this.urlState = urlState;
    }
}
