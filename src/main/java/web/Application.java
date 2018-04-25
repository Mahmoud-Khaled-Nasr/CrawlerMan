package web;

import model.URL;
import org.mongodb.morphia.query.FindOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import util.DatabaseDriver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@SpringBootApplication
@EnableScheduling
public class Application {

    static String SEED_FILE_NAME;
    static int MAX_DOUCMENTS_COUNT, NUMBER_OF_THREADS;

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

        SEED_FILE_NAME = args[0];
        MAX_DOUCMENTS_COUNT = Integer.parseInt(args[1]);
        NUMBER_OF_THREADS = Integer.parseInt(args[2]);

        util.DatabaseDriver.initializeDatabase();

        SpringApplication.run(Application.class);
    }

    static void update(String seedFileName, int maxURLsCount, int numberOfThreads) throws InterruptedException, FileNotFoundException {

        // Run Crawler
        Thread crawlerThread = new Thread(() -> {
            try {
                crawler.Crawler.crawl(seedFileName, maxURLsCount, numberOfThreads);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Run Indexer
        Thread indexerThread = new Thread(indexer.Indexer::index);

        // Waiting for them to finish
        crawlerThread.start();
        indexerThread.start();
        crawlerThread.join();
        indexerThread.join();

        // Update seeds
        List<URL> urls = DatabaseDriver.datastore.createQuery(model.URL.class).order("urlRank").asList(new FindOptions().limit(maxURLsCount / 4));
        try (PrintWriter file = new PrintWriter(seedFileName)) {
            for (URL url : urls) {
                file.println(url.getURL());
            }
        }
    }

}
