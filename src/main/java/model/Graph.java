package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Property;

import java.util.List;

@Entity
public class Graph {
    @Id
    private ObjectId _id;
    @Indexed(unique = true, background = true)
    private Integer urlId;
    @Property
    private List<Integer> children, parents;

    public Graph(Integer urlId, List<Integer> children, List<Integer> parents) {
        this.urlId = urlId;
        this.children = children;
        this.parents = parents;
    }
}
