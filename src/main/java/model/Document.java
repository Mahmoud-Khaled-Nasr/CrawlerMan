package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import java.util.List;

@Entity("Index")
public class Document {
    @Id
    private ObjectId _id;
    @Indexed(unique = true, background = true)
    private Integer urlId;
    private List<String> words;

    public Document(Integer urlId, List<String> words) {
        this.urlId = urlId;
        this.words = words;
    }

    public Document() {
    }
}
