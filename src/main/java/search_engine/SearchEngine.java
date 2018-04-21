package search_engine;

import indexer.Stemmer;
import model.Document;
import ranker.DynamicRanker;
import util.PathGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class SearchEngine {
    private static Collection<Integer> querySearch (String query){
        query = query.toLowerCase();
        List<String> queryStemmedWords = Stemmer.stem(query);

        return DynamicRanker.getRankSortedUrls(queryStemmedWords);
    }

    private static Collection<Integer> phraseSearch(String query) throws InterruptedException {
        List<String> phraseStemmedWords = Stemmer.stem(query);
        List<Document> documents = DynamicRanker.getPhraseDocuments(phraseStemmedWords);
        ExecutorService phraseSearch = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        LinkedBlockingQueue<Integer> results = new LinkedBlockingQueue<>();

        for (Document document : documents){
            int urlId = document.getUrlId();
            phraseSearch.execute(() -> {
                try {
                    String html = new String(Files.readAllBytes(PathGenerator
                            .generate("HTML", String.valueOf(urlId))));
                    if (html.toLowerCase().contains(query.toLowerCase())){
                        results.add(urlId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        phraseSearch.shutdown();
        assert phraseSearch.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return results;
    }

    // TODO better logic!
    public static Collection<Integer> search (String query) throws InterruptedException {
        if(query.startsWith("\"") && query.endsWith("\"")) {
            return phraseSearch(query.substring(0, query.length()-2));
        } else {
            return querySearch(query);
        }
    }
}
