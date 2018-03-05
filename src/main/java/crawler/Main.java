package crawler;

import util.Channel;

import java.util.HashSet;
import java.util.Set;

public class Main {

    private static void readSeed(String fileName) {
        //seed
    }

    public static void crawl(String seedFileName, int maxURLs, int numberOfLegs) {
        Channel<URL> URLs = new Channel<>();
        Channel<String> candidateURLs = new Channel<>();
        Set<String> visitedURLs = new HashSet<>();
        readSeed(seedFileName);

        for(int i = 0; i < numberOfLegs; i++) {
            new Thread(new Leg(URLs, candidateURLs)).start();
        }
        while(visitedURLs.size() < maxURLs) {
            String candidateURL = candidateURLs.take();
            if(!visitedURLs.contains(candidateURL)) {
                visitedURLs.add(candidateURL);
                URLs.put(new URL(candidateURL));
            }
        }
        candidateURLs.close();
        candidateURLs.clear();
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
