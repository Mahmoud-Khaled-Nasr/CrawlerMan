package web;

import model.URL;
import org.mongodb.morphia.query.FindOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import util.DatabaseDriver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@SpringBootApplication
public class Application {

    /**
     * The main method for the application as a whole.
     *
     * @param args Commandline arguments, must adhere to the restrictions specified
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        if (args.length != 3) {
            System.err.println("Usage: CrawlerMan <seed_file> <max_URLs_count> <number_of_threads>");
            System.exit(-1);
        }

        String seedFileName = args[0];
        int maxURLsCount = Integer.parseInt(args[1]);
        int numberOfThreads = Integer.parseInt(args[2]);

        util.DatabaseDriver.initializeDatabase();

        Thread crawlerThread = new Thread(() -> {
            try {
                crawler.Main.crawl(seedFileName, maxURLsCount, numberOfThreads);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread indexerThread = new Thread(indexer.Main::index);

        SpringApplication.run(Application.class);

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
