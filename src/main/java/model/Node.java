package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Property;

import java.util.Set;

@Entity
public class Node {
    @Id
    private ObjectId _id;
    @Indexed(unique = true, background = true)
    private Integer urlId;
    @Property
    private Set<Integer> children;

    public Node() {
    }

    public Node(Integer urlId, Set<Integer> children) {
        this.urlId = urlId;
        this.children = children;
    }

    public Integer getUrlId() {
        return urlId;
    }

    public Set<Integer> getChildren() {
        return children;
    }

    public void setChildren(Set<Integer> children) {
        this.children = children;
    }
}
