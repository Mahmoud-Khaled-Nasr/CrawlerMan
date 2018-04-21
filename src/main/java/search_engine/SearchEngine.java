package search_engine;

import indexer.Stemmer;
import model.Document;
import ranker.DynamicRanker;
import util.PathGenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SearchEngine {
    static List<Document> querySearch (String query){
        query = query.toLowerCase();
        List<String> queryStemmedWords = Stemmer.stem(query);

        List<Integer> urlIds = DynamicRanker.getRankSortedUrls(queryStemmedWords);
        return DatabaseController.getDocuments(urlIds);
    }

    @SuppressWarnings("unchecked") // I hate this line, but the casting is necessary
    static List<Document> phraseSearch (String query) throws InterruptedException {
        String loweredCaseQuery = query.toLowerCase();
        List<String> phraseStemmedWords = Stemmer.stem(query);
        List<Document> documents = DynamicRanker.getPhraseDocuments(phraseStemmedWords);
        ExecutorService phraseSearch = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        BlockingQueue<Document> results = new LinkedBlockingQueue<>();

        for (Document document : documents){
            phraseSearch.execute(() -> {
                try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(PathGenerator.generate("HTML", String.valueOf(urlId)).toFile()))) {
                    String html = ((Map<String, String>) stream.readObject()).get("body");
                    if (html.contains(loweredCaseQuery)){
                        results.add(document);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
        phraseSearch.shutdown();
        assert phraseSearch.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return Arrays.asList(results.toArray(new Document[results.size()]));
    }
}
