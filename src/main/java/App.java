import model.URL;
import org.mongodb.morphia.query.FindOptions;
import util.DatabaseDriver;
import util.PathGenerator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * The main class of the application.
 * Does
 */
public class App {

    /**
     * The main method for the application as a whole.
     *
     * @param args Commandline arguments, must adhere to the restrictions specified
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        if (args.length != 2) {
            System.err.println("Usage: CrawlerMan <seed_file> <max_URLs_count>");
            System.exit(-1);
        }

        String seedFileName = args[0];
        int maxURLsCount = Integer.parseInt(args[1]);

        util.DatabaseDriver.initializeDatabase();

        Thread crawlerThread = new Thread(() -> {
            try {
                crawler.Main.crawl(seedFileName, maxURLsCount);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread indexerThread = new Thread(indexer.Main::index);

        crawlerThread.start();
        indexerThread.start();
        crawlerThread.join();
        indexerThread.join();

        updateSeeds(seedFileName, maxURLsCount);
    }

    private static void updateSeeds(String seedFileName, int maxURLsCount) throws FileNotFoundException {
        List<URL> urls = DatabaseDriver.datastore.createQuery(model.URL.class).order("urlRank").asList(new FindOptions().limit(maxURLsCount / 4));
        try (PrintWriter file = new PrintWriter(seedFileName)) {
            for (URL url : urls) {
                file.println(url.getURL());
            }
        }
    }
}
