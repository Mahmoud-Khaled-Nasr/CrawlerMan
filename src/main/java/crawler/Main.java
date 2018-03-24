package crawler;

import crawler.robot.RobotMonitor;
import util.Pair;
import util.PathGenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Main {

    private static final String CRAWLER_STATE = "crawler state";

    private static List<String> readSeed(String seedFileName) throws IOException {
        List<String> urls = new LinkedList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(seedFileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                urls.add(line);
            }
        }
        return urls;
    }

    public static void crawl(String seedFileName, int maxURLsCount, int numberOfLegs) throws IOException {
        BlockingQueue<String> URLs = new LinkedBlockingQueue<>(readSeed(seedFileName));
        BlockingQueue<Pair<String, Set<String>>> candidateURLs = new LinkedBlockingQueue<>();
        Set<String> visitedURLs = new HashSet<>();
        PrintWriter graphEdges = new PrintWriter(PathGenerator.generate("graph").toFile(), "UTF-8");
        RobotMonitor robotMonitor = new RobotMonitor();
        maxURLsCount -= URLs.size();
        loadState(URLs, visitedURLs);

        for (int i = 0; i < numberOfLegs; i++) {
            new Thread(new Leg(URLs, candidateURLs, robotMonitor)).start();
        }
        while (numberOfLegs > 0) {
            try {
                Pair<String, Set<String>> candidateURLsSet = candidateURLs.take();
                if (candidateURLsSet.getKey().equals("")) {
                    numberOfLegs--;
                    continue;
                }
                graphEdges.println(candidateURLsSet.getKey());
                graphEdges.println(candidateURLsSet.getValue().size());
                for (String url : candidateURLsSet.getValue()) {
                    graphEdges.println(url);

                    if (visitedURLs.size() < maxURLsCount && !visitedURLs.contains(url)) {
                        visitedURLs.add(url);
                        URLs.put(url);
                        if (visitedURLs.size() == maxURLsCount) {
                            for (int i = 0; i < numberOfLegs; i++) {
                                URLs.put("");
                            }
                        }
                    }
                }
                saveState(URLs, visitedURLs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        graphEdges.close();
    }

    private static void saveState (BlockingQueue<String> URLs, Set<String> visitedURLs){
        final String TEMP_CRAWLER_STATE = "temp crawler state";
        File crawlerStateFile = null;
        File tempCrawlerStateFile = null;
        try {
            crawlerStateFile = PathGenerator.generate(CRAWLER_STATE).toFile();
            tempCrawlerStateFile = PathGenerator.generate(TEMP_CRAWLER_STATE).toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter printWriter = null;
        try {
            if (crawlerStateFile.exists() && crawlerStateFile.isFile()) {
                printWriter = new PrintWriter(tempCrawlerStateFile, "UTF-8");
            } else {
                printWriter = new PrintWriter(crawlerStateFile, "UTF-8");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] URLsArray;
        synchronized (URLs) {
            URLsArray = URLs.toArray(new String[URLs.size()]);
        }
        printWriter.println(URLsArray.length);
        for (String url: URLsArray){
            printWriter.println(url);
        }
        String[] visitedURLsArray = visitedURLs.toArray(new String[visitedURLs.size()]);
        printWriter.println(visitedURLs.size());
        for (String url: visitedURLsArray) {
            printWriter.println(url);
        }
        printWriter.close();
        if (crawlerStateFile.exists() && crawlerStateFile.isFile()) {
            try {
                Files.move(tempCrawlerStateFile.toPath(), crawlerStateFile.toPath(), REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadState (BlockingQueue<String> URLs, Set<String> visitedURLs){
        File file = null;
        try {
            file = PathGenerator.generate(CRAWLER_STATE).toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int URLsSize = scanner.nextInt();
        try {
            for (int i = 0; i < URLsSize; i++) {
                URLs.put(scanner.nextLine());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int visitedSetSize = scanner.nextInt();
        for (int i = 0; i < visitedSetSize; i++) {
            visitedURLs.add(scanner.nextLine());
        }
        scanner.close();
    }

    public static void main (String[] args) throws IOException {
        if(args.length != 3) {
            System.err.println("Usage: crawler <seed_file> <max_URLs_count> <number_of_legs>");
            System.exit(-1);
        }

        String seedFileName = args[0];
        int maxURLsCount = Integer.parseInt(args[1]);
        int numberOfLegs = Integer.parseInt(args[2]);
        crawl(seedFileName, maxURLsCount, numberOfLegs);
    }
}
