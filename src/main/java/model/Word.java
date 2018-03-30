package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Property;

import java.util.List;

@Entity("InvertedIndex")
public class Word {
    @Id
    private ObjectId _id;
    @Indexed(unique = true, background = true)
    private String word;
    @Property
    private List<Occurrence> occurrences;

    public Word(String word, List<Occurrence> occurrences) {
        this.word = word;
        this.occurrences = occurrences;
    }

    public Word() {
    }
}
