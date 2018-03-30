import sun.awt.windows.ThemeReader;
import util.DatabaseDriver;

import java.io.IOException;
import java.sql.Time;

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
        if(args.length != 4) {
            System.err.println("Usage: CrawlerMan <seed_file> <max_URLs_count> <damping_factor> <page_rank_iterations> ");
            System.exit(-1);
        }

        String seedFileName = args[0];
        int maxURLsCount = Integer.parseInt(args[1]);
        double dampingFactor = Double.parseDouble(args[2]);
        int pageRankIterations = Integer.parseInt(args[3]);

        DatabaseDriver.initializeDatabase();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                crawler.Main.crawl(seedFileName, maxURLsCount);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> indexer.Main.index(dampingFactor, pageRankIterations)).start();
    }
}
