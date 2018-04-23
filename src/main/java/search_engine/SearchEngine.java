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
    public static List<Result> search (String query, int page) throws InterruptedException {
        final int PAGE_SIZE = 10;
        List<URL> urls;
        List<Result> pagedResults = new LinkedList<>();
        if(query.startsWith("\"") && query.endsWith("\"")) {
            urls = phraseSearch(query.substring(1, query.length()-2));
        } else {
            urls = querySearch(query);
        }
        for (int i = page * PAGE_SIZE; i < urls.size() && i < page * PAGE_SIZE + PAGE_SIZE; i++) {
            pagedResults.add(getResultFromURL(urls.get(i)));
        }
        return pagedResults;
    }

    private static Result getResultFromURL (URL url){
        //TODO Change the title and snipped
        //TODO get the string of the snippet from the file
        return new Result(String.valueOf(url.getUrlId()), url.getURL(), String.valueOf(url.getUrlRank()));
    }
}
