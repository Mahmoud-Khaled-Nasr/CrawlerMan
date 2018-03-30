package indexer;

import ranker.StaticRanker;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class of the indexer package.
 * This class is intended to be used as static class with only static methods allowed.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * The main indexer method.
     * It indexes the new URLs sent by the crawler via the database channel.
     * @param dampingFactor The damping factor to be passed to the static ranker
     * @param pageRankIterations The page rank iterations count to be passed to the static ranker
     */
    public static void index(double dampingFactor, int pageRankIterations) {

        LOGGER.log(Level.INFO,"Indexer is starting!");

        ExecutorService stemmers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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

        stemmers.shutdown();
        StaticRanker.updateRanks(newLinks, dampingFactor, pageRankIterations);

        LOGGER.log(Level.INFO,"Indexer is shutting down normally!");
    }

    /**
     * A main method for the indexer to be run independently.
     * @param args Commandline arguments, must adhere to the restrictions specified
     */
    public static void main (String[] args) {
        if(args.length != 2) {
            System.err.println("Usage: indexer <damping_factor> <page_rank_iterations>");
            System.exit(-1);
        }

        double dampingFactor = Double.parseDouble(args[0]);
        int pageRankIterations = Integer.parseInt(args[1]);
        index(dampingFactor, pageRankIterations);
    }
}
