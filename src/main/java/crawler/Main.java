package crawler;

import crawler.robot.RobotMonitor;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class of the crawler package.
 * This class is intended to be used as static class with only static methods allowed.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final int NUMBER_OF_THREADS = 10;

    // Services
    private static final ExecutorService downloadersExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final CompletionService<Document> downloadersService = new ExecutorCompletionService<>(downloadersExecutor);
    private static final ExecutorService saversExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final RobotMonitor robotMonitor = new RobotMonitor();
    private static final Set<String> visitedURLs = new HashSet<>();
    private static final Set<String> savedURLs = new HashSet<>();
    private static int remainingURLsCount;

    /**
     * Submits a link to be downloaded later on.
     * @param url The URL to be downloaded
     */
    private synchronized static void submitDownload(String url) {
        visitedURLs.add(url);
        DatabaseController.crawling(url);
        downloadersService.submit(new Downloader(url));
    }

    /**
     * Submits a new link to be inspected.
     * The link will be submitted for download if it is allowed and has never been visited before and the crawler still hasn't reached.
     * @param link The link to be inspected
     */
    synchronized static void submitNewLink(String link) {
        if (!visitedURLs.contains(link)
                && remainingURLsCount > 0
                && robotMonitor.isAllowed(link)) {
            submitDownload(link);
            remainingURLsCount--;
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
    public static void crawl(String seedFileName, int maxURLsCount) throws IOException, InterruptedException {

        LOGGER.log(Level.INFO,"Crawler is starting!");

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
        remainingURLsCount = maxURLsCount - URLs.size();
        for (String url : URLs) {
            submitDownload(url);
        }

        // Main loop
        for (int i = 0; i < maxURLsCount; i++) {
            Document document;
            try {
                document = downloadersService.take().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
                remainingURLsCount++;
                continue;
            }

            // Normalization
            String url = document.location();
            if (savedURLs.contains(url)) {
                LOGGER.log(Level.INFO,"Ignoring a redirection duplicate " + url);
                remainingURLsCount++;
                continue;
            }

            visitedURLs.add(url);
            savedURLs.add(url);
            saversExecutor.execute(new Saver(document));
        }

        downloadersExecutor.shutdown();
        assert downloadersExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        saversExecutor.shutdown();
        assert saversExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        DatabaseController.clearState();
        LOGGER.log(Level.INFO,"Crawler is shutting down normally!");
    }

    /**
     * A main method for the crawler to be run independently.
     * @param args Commandline arguments, must adhere to the restrictions specified
     */
    public static void main (String[] args) throws IOException, InterruptedException {
        if(args.length != 2) {
            System.err.println("Usage: crawler <seed_file> <max_URLs_count>");
            System.exit(-1);
        }

        String seedFileName = args[0];
        int maxURLsCount = Integer.parseInt(args[1]);
        crawl(seedFileName, maxURLsCount);
    }
}
