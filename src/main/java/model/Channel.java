package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import util.DatabaseDriver;

import java.util.List;

@Entity()
public class Channel {
    @Id
    private ObjectId id;
    @Indexed(unique = true, background = true)
    private String URL;
    @Property
    private List<String> children;

    public Channel(String URL, List<String> children){
        this.URL = URL;
        this.children = children;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public String getURL() {
        return URL;
    }

    public List<String> getChildren() {
        return children;
    }
}
