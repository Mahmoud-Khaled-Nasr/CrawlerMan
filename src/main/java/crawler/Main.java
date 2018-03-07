package crawler;

import util.Channel;
import util.Pair;

import java.util.HashSet;
import java.util.Set;

public class Main {

    private static void readSeed(String fileName) {
        //seed
    }

    public static void crawl(String seedFileName, int maxURLs, int numberOfLegs) {
        Channel<String> URLs = new Channel<>();
        Channel<Pair<String, Set<String>>> candidateURLSets = new Channel<>();
        Set<String> visitedURLs = new HashSet<>();
        readSeed(seedFileName);

        for (int i = 0; i < numberOfLegs; i++) {
            new Thread(new Leg(URLs, candidateURLSets)).start();
        }
        while (visitedURLs.size() < maxURLs) {
            Pair<String, Set<String>> candidateURLSet = candidateURLSets.take();
            for (String url : candidateURLSet.getValue()) {
                // TODO save graph
                if (!visitedURLs.contains(url)) {
                    visitedURLs.add(url);
                    URLs.put(url);
                }
                // TODO save state
            }
        }
        candidateURLSets.close();
        candidateURLSets.clear();
    }

    public static void main (String[] args) {
        if(args.length != 4) { // needs checking
            System.err.println("argv.length = " + args.length);
        }

        String fileName = args[1];
        int maxURLsNumber = Integer.parseInt(args[2]);
        int legsNumber = Integer.parseInt(args[3]);
        crawl(fileName, maxURLsNumber, legsNumber);
    }
}
