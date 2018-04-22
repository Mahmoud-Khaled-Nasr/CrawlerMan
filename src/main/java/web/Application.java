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

        SpringApplication.run(Application.class);

        update(seedFileName, maxURLsCount, numberOfThreads);
    }


    private static void update(String seedFileName, int maxURLsCount, int numberOfThreads) throws InterruptedException, FileNotFoundException {

        // Run Crawler
        Thread crawlerThread = new Thread(() -> {
            try {
                crawler.Main.crawl(seedFileName, maxURLsCount, numberOfThreads);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Run Indexer
        Thread indexerThread = new Thread(indexer.Main::index);

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
