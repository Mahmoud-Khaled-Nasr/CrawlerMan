package search_engine;

import indexer.Stemmer;
import ranker.DynamicRanker;

import java.util.List;

public class SearchEngine {
    static void querySearch (String query){
        List<String> queryStemmedWords = Stemmer.stem(query);

        List<Integer> urlIds = DynamicRanker.getRankSortedUrls(queryStemmedWords);

    }

    static void phraseSearch (String query){
        List<String> phraseStemmedWords = Stemmer.stem(query);
        DynamicRanker.getPhraseUrlsIds(phraseStemmedWords);
    }
}
