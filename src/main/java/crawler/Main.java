package crawler;

import crawler.robot.RobotMonitor;
import util.Pair;
import util.PathGenerator;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

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
        BlockingQueue<String> URLs = new LinkedBlockingQueue<>();
        BlockingQueue<Pair<String, Set<String>>> candidateURLs = new LinkedBlockingQueue<>();
        Set<String> visitedURLs = new HashSet<>();
        PrintWriter graphEdges = new PrintWriter(PathGenerator.generate("graph").toFile(), "UTF-8");
        RobotMonitor robotMonitor = new RobotMonitor();
        maxURLsCount -= URLs.size();
        DatabaseController.getState(URLs, visitedURLs);
        if (URLs.isEmpty()){
            URLs.addAll(readSeed(seedFileName));
        }

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
                DatabaseController.insertState(URLs, visitedURLs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        graphEdges.close();
        DatabaseController.clearState();
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
