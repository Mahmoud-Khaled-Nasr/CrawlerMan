package search_engine;

import indexer.Stemmer;
import model.Document;
import model.URL;
import ranker.DynamicRanker;
import util.PathGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.*;

public class SearchEngine {
    private static List<URL> querySearch (String query){
        query = query.toLowerCase();
        List<String> queryStemmedWords = Stemmer.stem(query);

        List<Integer> resultUrls = DynamicRanker.getRankSortedUrls(queryStemmedWords);
        return DatabaseController.getUrls(resultUrls);
    }

    private static List<URL> phraseSearch(String query) throws InterruptedException {
        List<String> phraseStemmedWords = Stemmer.stem(query);
        List<Document> documents = DynamicRanker.getPhraseDocuments(phraseStemmedWords);
        ExecutorService phraseSearch = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        LinkedBlockingQueue<Integer> resultsIds = new LinkedBlockingQueue<>();

        for (Document document : documents){
            int urlId = document.getUrlId();
            phraseSearch.execute(() -> {
                try {
                    String html = new String(Files.readAllBytes(PathGenerator
                            .generate("HTML", String.valueOf(urlId))));
                    if (html.toLowerCase().contains(query.toLowerCase())){
                        resultsIds.add(urlId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        phraseSearch.shutdown();
        assert phraseSearch.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return DatabaseController.getUrls(resultsIds);

    }

    // TODO better logic!
    public static List<URL> search (String query) throws InterruptedException {
        if(query.startsWith("\"") && query.endsWith("\"")) {
            return phraseSearch(query.substring(1, query.length()-2));
        } else {
            return querySearch(query);
        }
    }
}
