package crawler;

import util.Channel;

import java.util.HashSet;
import java.util.Set;

public class Main {
    Channel<URL> URLs;
    Channel<String> candidateURLs;
    Set<String> visitedURLs;
    int maxURLs;

    private void readSeed(String fileName) {
        //seed
    }

    private void start(String seedFileName, int maxURLs, int numberOfLegs) {
        this.URLs = new Channel<>();
        this.candidateURLs = new Channel<>();
        this.visitedURLs = new HashSet<>();
        this.maxURLs = maxURLs;
        readSeed(seedFileName);

        for(int i = 0; i < numberOfLegs; i++) {
            new Thread(new Leg(URLs, candidateURLs)).start();
        }
    }

    public void main (String[] args) {
        if(args.length != 3) {
            System.err.println("argv.length = " + args.length);
        }

        String fileName = args[1];
        int maxURLsNumber = Integer.parseInt(args[2]);
        int legsNumber = Integer.parseInt(args[3]);
        start(fileName, maxURLsNumber, legsNumber);
    }
}
