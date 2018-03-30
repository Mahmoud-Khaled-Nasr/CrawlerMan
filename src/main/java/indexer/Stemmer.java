package indexer;

import opennlp.tools.stemmer.PorterStemmer;
import util.PathGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Stemmer class represents a runnable stemming task for a document.
 */
public class Stemmer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Stemmer.class.getName());

    final private int urlId;

    /**
     * Constructs an instance of a stemming task for the given document.
     * @param urlId The ID of the document to be stemmed
     */
    Stemmer(int urlId) {
        this.urlId = urlId;
    }

    @Override
    public void run() {
        LOGGER.log(Level.INFO,"Indexing " + urlId);
        try {
            String html = new String(Files.readAllBytes(PathGenerator.generate("HTML", String.valueOf(urlId))));
            String[] words = html.replaceAll("[^\\p{L} ]", " ").toLowerCase().split("\\s+");
            PorterStemmer porterStemmer = new PorterStemmer();
            List<String> stemmedWords = new LinkedList<>();
            for (String word : words) {
                String stemmedWord = porterStemmer.stem(word);
                if (stemmedWord.length() > 1) {
                    stemmedWords.add(stemmedWord);
                }
            }
            DatabaseController.updateDocument(urlId, stemmedWords);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO,"Indexed " + urlId);
    }
}
