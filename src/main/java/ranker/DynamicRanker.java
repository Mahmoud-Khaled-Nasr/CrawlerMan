package ranker;

import model.Document;
import model.Occurrence;
import model.Word;

import java.util.*;

public class DynamicRanker {

    static public List<Document> getPhraseDocuments(List<String> query) {
        return DatabaseController.getUrlsContainingWords(query);
    }

    static public List<Integer> getRankSortedUrls (List<String> query){
        List<Word> words = DatabaseController.getWords(query);
        Map<Integer, Double> relevance = new TreeMap<>();

        for (Word word : words){
            List<Occurrence> occurrenceList = word.getOccurrences();
            for (Occurrence occurrence : occurrenceList){
                int urlId = occurrence.getUrlId();
                Double tf = (double)occurrence.getCount() / DatabaseController.getDocumentLength(urlId);
                Double idf = Math.log(DatabaseController.getTotalNumberOfDocuments() / (double)occurrenceList.size());
                Double staticRank = DatabaseController.getUrlPageRank(urlId);
                relevance.put(urlId, relevance.getOrDefault(urlId, 0.0) + tf * idf * staticRank);
            }
        }

        return sortRelevanceMap(relevance);
    }

    private static List<Integer> sortRelevanceMap (Map<Integer, Double> relevance){
        List<Integer> sortedIds = new LinkedList<>();
        List<Double> values = new ArrayList<>(relevance.values());
        Collections.sort(values);
        for (Double value : values){
            for (Integer key : relevance.keySet()){
                if (relevance.get(key).equals(value)){
                    sortedIds.add(key);
                    break;
                }
            }
        }
        return sortedIds;
    }

}
