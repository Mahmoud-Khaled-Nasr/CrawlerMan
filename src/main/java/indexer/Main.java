package indexer;

import util.Pair;
import util.PathGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    private static double DAMPING_FACTOR = 0.5;
    private static int PAGE_RANK_ITERATIONS = 100;

    public static void index() throws IOException {
        // TODO need to find a way to incrementally update the graph
        Map<Integer, List<Integer>> inDegree = new HashMap<>();
        Map<Integer, Integer> outDegree = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(PathGenerator.generate("graph")))) {
            String url;
            while ((url = bufferedReader.readLine()) != null) {
                int urlId = url.hashCode();
                // TODO word stemming of documents and adding them to DB
                int count = Integer.parseInt(bufferedReader.readLine());
                outDegree.put(urlId, count);
                for (int i = 0; i < count; i++) {
                    int linkId = bufferedReader.readLine().hashCode();
                    if (!inDegree.containsKey(linkId)) {
                        inDegree.put(linkId, new LinkedList<>());
                    }
                    inDegree.get(linkId).add(urlId);
                }
            }
        }

        Map<Integer, Double> ranks = new HashMap<>();
        for (int i = 0; i < PAGE_RANK_ITERATIONS; i++) {
            for (int urlId : outDegree.keySet()) {
                double rank = 0;
                for (int linkId : inDegree.get(urlId)) {
                    rank += ranks.getOrDefault(linkId, 0.0) / outDegree.get(linkId);
                }
                rank = (1 - DAMPING_FACTOR) + DAMPING_FACTOR * rank;
                ranks.put(urlId, rank);
            }
        }
        // TODO save the ranks to DB
    }

    public static void main (String[] args) throws IOException {
        index();
    }
}
