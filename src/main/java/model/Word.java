package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Embedded;

import java.util.LinkedList;
import java.util.List;

@Entity
public class Word {
    @Id
    private ObjectId _id;
    @Indexed(unique = true, background = true)
    private String word;
    @Embedded
    private List<Occurrence> occurrences;

    public Word(String word) {
        this.word = word;
        this.occurrences = new LinkedList<>();
    }

    public Word() {
    }

    public void addNewOccurrence(Occurrence occurrence){
        occurrences.add(occurrence);
    }

    public String getWord() {
        return word;
    }

    public List<Occurrence> getOccurrences() {
        return occurrences;
    }
}
