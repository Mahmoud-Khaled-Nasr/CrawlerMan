package crawler;

import crawler.robot.RobotMonitor;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * The main class of the crawler package.
 * This class is intended to be used as static class with only static methods allowed.
 */
public class Crawler {

    private static final Logger LOGGER = Logger.getLogger(Crawler.class.getName());

    // Services
    private static ExecutorService downloadersExecutor;
    private static CompletionService<Document> downloadersService;
    private static ExecutorService saversExecutor;
    private static final RobotMonitor robotMonitor = new RobotMonitor();
    private static final Set<String> visitedURLs = new HashSet<>();
    private static final Set<String> savedURLs = new HashSet<>();
    private static final Object lock = new Object();
    private static int remainingURLsCount;

    /**
     * Submits a new link to be inspected.
     * The link will be submitted for download if it is allowed and has never been visited before and the crawler still hasn't reached.
     * @param link The link to be inspected
     */
    static void submitNewLink(String link) {
        int hashIndex = link.indexOf("#");
        if (hashIndex != -1) {
            link = link.substring(0, hashIndex);
        }
        synchronized (lock) {
            if (!downloadersExecutor.isShutdown()
                    && !visitedURLs.contains(link)
                    && robotMonitor.isAllowed(link)
                    && remainingURLsCount > 0) {
                visitedURLs.add(link);
                DatabaseController.crawling(link);
                downloadersService.submit(new Downloader(link));
                remainingURLsCount--;
            }
        }
    }

    /**
     * The main crawler method.
     * It initializes/loads the crawler state and schedules new links to be visited.
     * @param seedFileName The name of the seed file
     * @param maxURLsCount The maximum number of URLs to be visited
     * @throws IOException If an error occurred during reading the seed file
     * @throws InterruptedException If an external thread interrupted the internal threads
     */
    public static void crawl(String seedFileName, int maxURLsCount, int numberOfThreads) throws IOException, InterruptedException {

        LOGGER.info("Crawler is starting!");

        downloadersExecutor = Executors.newFixedThreadPool(numberOfThreads / 2);
        downloadersService = new ExecutorCompletionService<>(downloadersExecutor);
        saversExecutor = Executors.newFixedThreadPool(numberOfThreads);

        // Load state
        Set<String> URLs = new HashSet<>();
        DatabaseController.loadState(URLs, visitedURLs);
        maxURLsCount -= visitedURLs.size();
        if (URLs.isEmpty()) { // Read seed file
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(seedFileName))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    URLs.add(line);
                }
            }
        }

        // Setup
        remainingURLsCount = maxURLsCount;
        for (String url : URLs) {
            submitNewLink(url);
        }

        // Main loop
        for (int i = 0; i < maxURLsCount; i++) {
            Document document;
            try {
                document = downloadersService.take().get();
            } catch (ExecutionException e) {
                synchronized (lock) {
                    if (remainingURLsCount > 0) {
                        remainingURLsCount++;
                        maxURLsCount++;
                    }
                }
                continue;
            }

            // Normalization
            String url = document.location();
            if (savedURLs.contains(url)) {
                LOGGER.info("Ignoring a redirection duplicate " + url);
                synchronized (lock) {
                    if (remainingURLsCount > 0) {
                        remainingURLsCount++;
                        maxURLsCount++;
                    }
                }
                continue;
            }

            visitedURLs.add(url);
            savedURLs.add(url);
            saversExecutor.execute(new Saver(document));
        }

        LOGGER.info("Crawler is trying to shut down normally!");
        downloadersExecutor.shutdown();
        assert downloadersExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        saversExecutor.shutdown();
        assert saversExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        DatabaseController.clearState();
        DatabaseController.closeChannel();
        LOGGER.info("Crawler is shutting down normally!");
    }

    /**
     * A main method for the crawler to be run independently.
     * @param args Commandline arguments, must adhere to the restrictions specified
     */
    public static void main (String[] args) throws IOException, InterruptedException {
        if(args.length != 3) {
            System.err.println("Usage: crawler <seed_file> <max_URLs_count> <number_of_threads>");
            System.exit(-1);
        }

        String seedFileName = args[0];
        int maxURLsCount = Integer.parseInt(args[1]);
        int numberOfThreads = Integer.parseInt(args[2]);
        util.DatabaseDriver.initializeDatabase();
        crawl(seedFileName, maxURLsCount, numberOfThreads);
    }
}
