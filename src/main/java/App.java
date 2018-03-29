import util.DatabaseDriver;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length != 4) {
            System.err.println("Usage: CrawlerMan <seed_file> <max_URLs_count> <number_of_legs>");
            System.exit(-1);
        }

        String seedFileName = args[0];
        int maxURLsCount = Integer.parseInt(args[1]);
        double dampingFactor = Double.parseDouble(args[2]);
        int pageRankIterations = Integer.parseInt(args[3]);

        DatabaseDriver.initializeDatabase();

        crawler.Main.crawl(seedFileName, maxURLsCount);
        indexer.Main.index(dampingFactor, pageRankIterations);
    }
}
