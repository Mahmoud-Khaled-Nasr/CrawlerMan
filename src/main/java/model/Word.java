package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Property;

import java.util.Map;

@Entity
public class Word {
    @Id
    private ObjectId _id;
    @Indexed(unique = true, background = true)
    private String word;
    @Property
    private Map<Integer, Integer> urls;

    public Word(String word, Map<Integer, Integer> urls) {
        this.word = word;
        this.urls = urls;
    }

    public Word() {
    }

    public Map<Integer, Integer> getURLs() {
        return urls;
    }
}
