package search_engine;

import indexer.Stemmer;
import model.Document;
import ranker.DynamicRanker;
import util.PathGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class SearchEngine {
    static List<Document> querySearch (String query){
        query = query.toLowerCase();
        List<String> queryStemmedWords = Stemmer.stem(query);

        List<Integer> urlIds = DynamicRanker.getRankSortedUrls(queryStemmedWords);
        return DatabaseController.getDocuments(urlIds);
    }

    static List<Document> phraseSearch (String query) throws InterruptedException {
        String loweredCaseQuery = query.toLowerCase();
        List<String> phraseStemmedWords = Stemmer.stem(query);
        List<Document> documents = DynamicRanker.getPhraseDocuments(phraseStemmedWords);
        ExecutorService phraseSearch = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        BlockingQueue<Document> results = new LinkedBlockingQueue<>();

        for (Document document : documents){
            phraseSearch.execute(() -> {
                try {
                    String html = new String(Files.readAllBytes(PathGenerator
                            .generate("HTML", String.valueOf(document.getUrlId()))));
                    if (html.contains(loweredCaseQuery)){
                        results.add(document);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        phraseSearch.shutdown();
        assert phraseSearch.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return Arrays.asList(results.toArray(new Document[results.size()]));
    }
}
