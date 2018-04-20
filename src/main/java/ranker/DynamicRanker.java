package ranker;

import model.Occurrence;
import model.Word;

import java.util.*;

public class DynamicRanker {

    static public List<Integer> getPhraseUrlsIds (List<String> query) {
        DatabaseController.getUrlsContainingWords(query);


        return null;

    }

    static public List<Integer> getRankSortedUrls (List<String> query){
        List<Word> words = DatabaseController.getWords(query);
        Map<Integer, Double> relevance = new HashMap<>();

        for (Word word : words){
            List<Occurrence> occurrenceList = word.getOccurrences();
            for (Occurrence occurrence : occurrenceList){
                int urlId = occurrence.getUrlId();
                Double tf = (double)occurrence.getCount() / DatabaseController.getDocumentLength(urlId);
                //TODO need to log the number here
                Double idf = DatabaseController.getTotalNumberOfDocuments() / (double)occurrenceList.size();
                relevance.put(urlId, relevance.getOrDefault(urlId, 0.0) + tf * idf);
            }
        }

        List<Integer> orderedUrlIds = sortRelevanceMap(relevance);

        return orderedUrlIds;
    }

    private static List<Integer> sortRelevanceMap (Map<Integer, Double> relevance){
        List<Double> values = new ArrayList<>();
        List<Integer> sortedIds = new LinkedList<>();
        values.addAll(relevance.values());
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
