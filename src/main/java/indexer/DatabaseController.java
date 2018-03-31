package indexer;

import model.*;
import org.mongodb.morphia.FindAndModifyOptions;
import util.DatabaseDriver;

import java.util.*;

class DatabaseController {

    static void updateDocument(int urlId, List<String> words){
        Map<String, Integer> newWords = new HashMap<>();
        for (String word : words) {
            newWords.put(word, newWords.getOrDefault(word, 0) + 1);
        }
        Document document = DatabaseDriver.datastore.createQuery(Document.class).field("urlId").equal(urlId).get();
        if (document == null) {
            document = new Document(urlId, newWords);
            DatabaseDriver.saveRecord(document);
        } else {
            Map<String, Integer> oldWords = document.getWords();
            document.setWords(newWords);
            DatabaseDriver.saveRecord(document);

            newWords.entrySet().removeAll(oldWords.entrySet());
            oldWords.entrySet().removeAll(newWords.entrySet());

            for (String oldWord : oldWords.keySet()) {
                Word word = DatabaseDriver.datastore.createQuery(Word.class).field("word").equal(oldWord).get();
                List<Occurrence> occurrences = word.getOccurrences();
                for (Occurrence occurrence : occurrences) {
                    if (occurrence.getUrlId().equals(urlId)) {
                        occurrences.remove(occurrence);
                        break;
                    }
                }
                DatabaseDriver.saveRecord(word);
            }
        }
        for (String newWord : newWords.keySet()) {
            DatabaseDriver.datastore.findAndModify(
                    DatabaseDriver.datastore.createQuery(Word.class).field("word").equal(newWord),
                    DatabaseDriver.datastore.createUpdateOperations(Word.class).push("occurrences", new Occurrence(urlId, newWords.get(newWord))),
                    new FindAndModifyOptions().upsert(true));
        }
    }

    static void insertURL(int id, String url){
        if (DatabaseDriver.datastore.createQuery(URL.class).field("url").equal(url).asList().isEmpty()) {
            DatabaseDriver.saveRecord(new URL(id, url));
        }
    }

    static String receiveURL (Set<String> links){
        while (DatabaseDriver.datastore.createQuery(Channel.class).count() == 0) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Channel channel = DatabaseDriver.datastore.createQuery(Channel.class).get();
        DatabaseDriver.datastore.delete(channel);
        if (channel.getURL().equals("")
                && DatabaseDriver.datastore.createQuery(Channel.class).count() != 0) {
            Channel tempChannel = DatabaseDriver.datastore.createQuery(Channel.class).get();
            DatabaseDriver.datastore.delete(tempChannel);
            DatabaseDriver.saveRecord(channel);
            channel = tempChannel;
        }
        links.addAll(channel.getChildren());
        return channel.getURL();
    }
}
