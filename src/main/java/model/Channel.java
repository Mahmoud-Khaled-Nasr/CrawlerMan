package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import util.DatabaseDriver;

import java.util.List;

@Entity(DatabaseDriver.COMMUNICATION_COLLECTION)
public class Channel {
    @Id
    private ObjectId id;
    @Indexed(unique = true, background = true)
    public String URL;
    @Property
    public List<String> children;

    public Channel(String URL, List<String> children){
        this.URL = URL;
        this.children = children;
    }
    public Channel(){}
}
