package indexer;

import opennlp.tools.stemmer.PorterStemmer;
import util.PathGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Main {

    private static List<String> stemHTML(int urlId) throws IOException {
        String html = new String(Files.readAllBytes(PathGenerator.generate("HTML", String.valueOf(urlId))));
        String[] words = html.replaceAll("[^\\p{L} ]", " ").toLowerCase().split("\\s+");
        PorterStemmer porterStemmer = new PorterStemmer();
        List<String> stemmedWords = new LinkedList<>();
        for (String word : words) {
            String stemmedWord = porterStemmer.stem(word);
            if (stemmedWord.length() > 1) {
                stemmedWords.add(stemmedWord);
            }
        }
        return stemmedWords;
    }

    public static void index(double dampingFactor, int pageRankIterations) throws IOException {
        // TODO need to find a way to incrementally update the graph
        Map<Integer, List<Integer>> inDegree = new HashMap<>();
        Map<Integer, Integer> outDegree = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(PathGenerator.generate("graph").toFile()))) {
            String url;
            while ((url = bufferedReader.readLine()) != null) {
                int urlId = url.hashCode();
                List<String> stemmedWords = stemHTML(urlId);
                System.out.println(stemmedWords);
                // TODO add stemmed words to DB -> (urlId, stemmedWords)
                int count = Integer.parseInt(bufferedReader.readLine());
                outDegree.put(urlId, count);
                inDegree.putIfAbsent(urlId, new LinkedList<>());
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
        for (int i = 0; i < pageRankIterations; i++) {
            for (int urlId : outDegree.keySet()) {
                double rank = 0;
                for (int linkId : inDegree.get(urlId)) {
                    rank += ranks.getOrDefault(linkId, 0.0) / outDegree.get(linkId);
                }
                rank = (1 - dampingFactor) + dampingFactor * rank;
                ranks.put(urlId, rank);
            }
        }
        // TODO save the ranks to DB
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
