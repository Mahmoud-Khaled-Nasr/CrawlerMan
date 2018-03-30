package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.List;

@Entity(cap = @CappedAt(100))
public class Channel {
    @Id
    public ObjectId id;
    @Indexed(unique = true, background = true)
    private String url;
    @Property
    private List<String> children;

    public Channel(String url, List<String> children){
        this.url = url;
        this.children = children;
    }

    public Channel(){}

    public String getURL() {
        return url;
    }

    public List<String> getChildren() {
        return children;
    }
}
