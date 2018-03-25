package indexer;

import ranker.StaticRanker;
import util.PathGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void index(double dampingFactor, int pageRankIterations) throws IOException {

        ExecutorService stemmers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Map<Integer, Set<Integer>> newLinks = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(PathGenerator.generate("graph.temp").toFile()))) {
            String url;
            while ((url = bufferedReader.readLine()) != null) {
                int urlId = url.hashCode();
                stemmers.execute(new Stemmer(urlId));
                int count = Integer.parseInt(bufferedReader.readLine());
                newLinks.put(urlId, new HashSet<>());
                for (int i = 0; i < count; i++) {
                    newLinks.get(urlId).add(bufferedReader.readLine().hashCode());
                }
            }
        }
        stemmers.shutdown();
        StaticRanker.updateRanks(newLinks, dampingFactor, pageRankIterations);
    }

    public static void main (String[] args) throws IOException {
        if(args.length != 2) {
            System.err.println("Usage: indexer <damping_factor> <page_rank_iterations>");
            System.exit(-1);
        }

        double dampingFactor = Double.parseDouble(args[0]);
        int pageRankIterations = Integer.parseInt(args[1]);
        index(dampingFactor, pageRankIterations);
    }
}
