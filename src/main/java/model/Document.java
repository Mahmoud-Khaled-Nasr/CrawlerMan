package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import java.util.Map;

@Entity
public class Document {
    @Id
    private ObjectId _id;
    @Indexed(unique = true, background = true)
    private Integer urlId;
    private Map<String, Integer> words;

    public Document(Integer urlId, Map<String, Integer> words) {
        this.urlId = urlId;
        this.words = words;
    }

    public Document() {
    }

    public Map<String, Integer> getWords() {
        return words;
    }

    public void setWords(Map<String, Integer> words) {
        this.words = words;
    }
}
