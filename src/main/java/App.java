import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        if(args.length != 5) {
            System.err.println("Usage: CrawlerMan <seed_file> <max_URLs_count> <number_of_legs>");
            System.exit(-1);
        }

        String seedFileName = args[0];
        int maxURLsCount = Integer.parseInt(args[1]);
        int numberOfLegs = Integer.parseInt(args[2]);
        double dampingFactor = Double.parseDouble(args[3]);
        int pageRankIterations = Integer.parseInt(args[4]);

        crawler.Main.crawl(seedFileName, maxURLsCount, numberOfLegs);
        indexer.Main.index(dampingFactor, pageRankIterations);
    }
}
