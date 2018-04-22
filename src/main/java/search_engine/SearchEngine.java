package search_engine;

import indexer.Stemmer;
import model.Document;
import model.URL;
import ranker.DynamicRanker;
import util.PathGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class SearchEngine {
    private static List<Result> querySearch (String query){
        query = query.toLowerCase();
        List<String> queryStemmedWords = Stemmer.stem(query);

        List<Integer> resultUrls = DynamicRanker.getRankSortedUrls(queryStemmedWords);
        return getSearchResults(DatabaseController.getUrls(resultUrls));
    }

    private static List<Result> phraseSearch(String query) throws InterruptedException {
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
        return getSearchResults(DatabaseController
                .getUrls(Arrays.asList(resultsIds.toArray(new Integer[resultsIds.size()]))));

    }

    // TODO better logic!
    public static List<Result> search (String query) throws InterruptedException {
        if(query.startsWith("\"") && query.endsWith("\"")) {
            return phraseSearch(query.substring(0, query.length()-2));
        } else {
            return querySearch(query);
        }
    }

    private static List<Result> getSearchResults (List<URL> urls){
        List<Result> results = new LinkedList<>();
        for (URL url : urls){
            //TODO Change the title and snipped
            results.add(new Result("", url.getURL(), ""));
        }
        return results;
    }
}
