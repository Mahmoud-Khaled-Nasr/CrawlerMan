package indexer;

import crawler.Crawler;
import model.*;
import util.DatabaseDriver;

import java.util.*;
import java.util.logging.Logger;

class DatabaseController {

    private static final Logger LOGGER = Logger.getLogger(Crawler.class.getName());

    static void updateDocument(int urlId, List<String> words) {
        Map<String, Integer> countedWords = new TreeMap<>();
        //Count the frequency of a word in document
        for (String word : words) {
            countedWords.put(word, countedWords.getOrDefault(word, 0) + 1);
        }
        Document document = DatabaseDriver.datastore.createQuery(Document.class).field("urlId").equal(urlId).get();
        //Check if the document existed before
        if (document == null) {
            //Create new document
            DatabaseDriver.saveRecord(new Document(urlId, countedWords));
            List<Word> existingWords = DatabaseDriver.datastore.createQuery(Word.class).field("word")
                    .in(countedWords.keySet()).asList();
            List<Word> newWords = new LinkedList<>();
            //update the old words
            for (Word word : existingWords) {
                word.addNewOccurrence(new Occurrence(urlId, countedWords.get(word.getWord())));
                countedWords.remove(word.getWord());
            }
            //create new words
            for (String word : countedWords.keySet()) {
                Word newRecord = new Word(word);
                newRecord.addNewOccurrence(new Occurrence(urlId, countedWords.get(word)));
                newWords.add(newRecord);
            }
            //Save the new and old words to the DB
            DatabaseDriver.datastore.save(existingWords);
            DatabaseDriver.datastore.save(newWords);
        } else {
            //if the document exists
            Map<String, Integer> currentDocumentWords = document.getWords();
            //Get all words in this document
            List<Word> currentDocumentWordsObjectList = DatabaseDriver.datastore.createQuery(Word.class).field("word")
                    .in(currentDocumentWords.keySet()).asList();

            List<Word> updatedWords = new LinkedList<>();
            List<Word> deletedWords = new LinkedList<>();
            List<Word> createdWords = new LinkedList<>();
            List<Word> deletedOccurrences = new LinkedList<>();
            for (Word word : currentDocumentWordsObjectList) {
                if (countedWords.get(word.getWord()) == null) {
                    //delete the old occurrence that don't exist anymore AND delete the word if there is no more occurrences
                    if (word.getOccurrences().size() <= 1) {
                        deletedWords.add(word);
                    } else {
                        for (Occurrence occurrence : word.getOccurrences()) {
                            if (occurrence.getUrlId().equals(urlId)) {
                                word.removeOccurrence(occurrence);
                                deletedOccurrences.add(word);
                                break;
                            }
                        }
                    }
                } else {
                    //update the words that still exists in the document
                    for (Occurrence occurrence : word.getOccurrences()) {
                        if (occurrence.getUrlId().equals(urlId)) {
                            occurrence.setCount(countedWords.get(word.getWord()));
                            updatedWords.add(word);
                            break;
                        }
                    }
                    countedWords.remove(word.getWord());
                }
            }
            //create the new occurrence of words from the new document OR create new word entirely
            List<Word> existingNewDocumentWords = DatabaseDriver.datastore.createQuery(Word.class).field("word")
                    .in(countedWords.keySet()).asList();

            //The new word already exists just add the new occurrence
            for (Word word : existingNewDocumentWords) {
                word.addNewOccurrence(new Occurrence(urlId, countedWords.get(word.getWord())));
                countedWords.remove(word.getWord());
            }

            //Create a new word entirely
            for (String word : countedWords.keySet()) {
                Word newRecord = new Word(word);
                newRecord.addNewOccurrence(new Occurrence(urlId, countedWords.get(word)));
                createdWords.add(newRecord);
                countedWords.remove(word);
            }

            //To check if there is no updates to add to the DB
            List<Word> queriedWords = new LinkedList<>();;
            if (!deletedWords.isEmpty()){
                DatabaseDriver.datastore.delete(Word.class, deletedWords);
            }
            if (!deletedOccurrences.isEmpty()){
                queriedWords.addAll(deletedOccurrences);
            }
            if (!createdWords.isEmpty()){
                queriedWords.addAll(createdWords);
            }
            if (!updatedWords.isEmpty()){
                queriedWords.addAll(updatedWords);
            }
            if (!existingNewDocumentWords.isEmpty()){
                queriedWords.addAll(existingNewDocumentWords);
            }
            if (!queriedWords.isEmpty()) {
                DatabaseDriver.datastore.save(queriedWords);
            }
        }
    }

    static void insertURL(int id, String url){
        if (DatabaseDriver.datastore.createQuery(URL.class).field("url").equal(url).asList().isEmpty()) {
            DatabaseDriver.saveRecord(new URL(id, url));
        }
    }

    static String receiveURL (Set<String> links){
        Channel channel = DatabaseDriver.datastore.createQuery(Channel.class).get();
        while (channel == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel = DatabaseDriver.datastore.createQuery(Channel.class).get();
        }

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
