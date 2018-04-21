package indexer;

import opennlp.tools.stemmer.PorterStemmer;
import util.PathGenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The Stemmer class represents a runnable stemming task for a document.
 */
public class Stemmer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Stemmer.class.getName());

    private static final PorterStemmer porterStemmer = new PorterStemmer();

    final private int urlId;

    public static List<String> stem(String text) {
        String[] words = text.replaceAll("[^\\p{L} ]", " ").toLowerCase().split("\\s+");
        List<String> stemmedWords = new LinkedList<>();
        for (String word : words) {
            String stemmedWord = porterStemmer.stem(word);
            if (stemmedWord.length() > 1) {
                stemmedWords.add(stemmedWord);
            }
        }
        return stemmedWords;
    }

    /**
     * Constructs an instance of a stemming task for the given document.
     * @param urlId The ID of the document to be stemmed
     */
    Stemmer(int urlId) {
        this.urlId = urlId;
    }

    @Override
    @SuppressWarnings("unchecked") // I hate this line, but the casting is necessary
    public void run() {
        LOGGER.info("Indexing " + urlId);
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(PathGenerator.generate("HTML", String.valueOf(urlId)).toFile()))) {
            Map<String, String> document = (Map<String, String>) stream.readObject();
            DatabaseController.updateDocument(urlId, stem(document.get("body")), document.get("title"), document.get("description"));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        LOGGER.info("Indexed " + urlId);
    }
}
