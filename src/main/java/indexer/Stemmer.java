package indexer;

import opennlp.tools.stemmer.PorterStemmer;
import util.PathGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The Stemmer class represents a runnable stemming task for a document.
 */
public class Stemmer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Stemmer.class.getName());

    final private int urlId;

    public static List<String> stem(String text) {
        PorterStemmer porterStemmer = new PorterStemmer();

        String[] words = text.replaceAll("[^\\p{L} ]", " ").toLowerCase().split("\\s+");
        List<String> stemmedWords = new LinkedList<>();
        String stemmedWord;
            for (String word : words) {
                try {
                    stemmedWord = porterStemmer.stem(word.toLowerCase());
                }catch (ArrayIndexOutOfBoundsException e){
                    LOGGER.warning("The stemmer bug at " + word);
                    continue;
                }
                if (stemmedWord.length() > 1 ) {
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
    public void run() {
        LOGGER.info("Indexing " + urlId);
        try {
            String html = new String(Files.readAllBytes(PathGenerator.generate("HTML", String.valueOf(urlId))));
            Main.updateDocument(urlId, stem(html));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Indexed " + urlId);
    }
}
