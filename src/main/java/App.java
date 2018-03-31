import java.io.IOException;

/**
 * The main class of the application.
 * Does
 */
public class App {

    /**
     * The main method for the application as a whole.
     * @param args Commandline arguments, must adhere to the restrictions specified
     */
    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("Usage: CrawlerMan <seed_file> <max_URLs_count>");
            System.exit(-1);
        }

        String seedFileName = args[0];
        int maxURLsCount = Integer.parseInt(args[1]);

        util.DatabaseDriver.initializeDatabase();

        new Thread(() -> {
            try {
                crawler.Main.crawl(seedFileName, maxURLsCount);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(indexer.Main::index).start();
    }
}
