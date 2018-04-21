package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Property;

import java.util.Map;

@Entity
public class Document {
    @Id
    private ObjectId _id;
    @Indexed(unique = true, background = true)
    private Integer urlId;
    @Property
    private Map<String, Integer> words;
    @Property
    private String title, description;

    public Document(Integer urlId, Map<String, Integer> words, String title, String description) {
        this.urlId = urlId;
        this.words = words;
        this.title = title;
        this.description = description;
    }

    public Document() {
    }

    public Integer getUrlId() {
        return urlId;
    }

    public Map<String, Integer> getWords() {
        return words;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setWords(Map<String, Integer> words) {
        this.words = words;
    }
}
