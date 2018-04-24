package indexer;

import ranker.StaticRanker;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * The main class of the indexer package.
 * This class is intended to be used as static class with only static methods allowed.
 */
public class Indexer {

    private static final Logger LOGGER = Logger.getLogger(Indexer.class.getName());
    private static ExecutorService updateDocumentsDaemon;
    /**
     * The main indexer method.
     * It indexes the new URLs sent by the crawler via the database channel.
     */
    public static void index() {

        LOGGER.info("Indexer is starting!");

        updateDocumentsDaemon = Executors.newSingleThreadExecutor();

        ExecutorService stemmers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
        Map<Integer, Set<Integer>> newLinks = new HashMap<>();

        Set<String> links = new HashSet<>();
        String url;
        while (!(url = DatabaseController.receiveURL(links)).equals("")) {
            int urlId = url.hashCode();
            DatabaseController.insertURL(urlId, url);
            stemmers.execute(new Stemmer(urlId));
            Set<Integer> linksIds = new HashSet<>();
            for (String link : links) {
                linksIds.add(link.hashCode());
            }
            newLinks.put(urlId, linksIds);
        }

        LOGGER.info("Indexer is trying to shut down normally!");
        stemmers.shutdown();
        StaticRanker.updateRanks(newLinks);
        try {
            assert updateDocumentsDaemon.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updateDocumentsDaemon.shutdown();
        LOGGER.info("Indexer is shutting down normally!");
    }

    static void updateDocument (int urlId, List<String> words){
        updateDocumentsDaemon.execute(() -> DatabaseController.updateDocument(urlId, words));
    }

    /**
     * A main method for the indexer to be run independently.
     */
    public static void main (String[] args) {
        util.DatabaseDriver.initializeDatabase();
        index();
    }
}
