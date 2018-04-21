package ranker;

import model.*;
import org.mongodb.morphia.query.Query;
import util.DatabaseDriver;

import java.util.*;

class DatabaseController {

    static void updatePageRanks(Map<Integer, Double> ranks){
        List<URL> urls = DatabaseDriver.datastore.createQuery(URL.class).field("urlId").in(ranks.keySet()).asList();
        for (URL url : urls){
            url.setUrlRank(ranks.get(url.getUrlId()));
        }
        DatabaseDriver.datastore.save(urls);
    }

    static List<Node> getGraph(){
        return DatabaseDriver.datastore.createQuery(Node.class).asList();
    }

    static void saveGraph(List<Node> graph){
        DatabaseDriver.datastore.save(graph);
    }

    static List<Word> getWords(List<String> words){
        return DatabaseDriver.datastore.createQuery(Word.class).field("word").in(words).asList();
    }

    static int getDocumentLength (int documentId){
        Document document = DatabaseDriver.datastore.createQuery(Document.class).field("urlId").equal(documentId).get();
        List<Integer>wordsCount = new ArrayList<>(document.getWords().values());
        int documentLength = 0;
        for (Integer count : wordsCount){
            documentLength += count;
        }
        return documentLength;
    }

    static long getTotalNumberOfDocuments (){
        return DatabaseDriver.datastore.createQuery(Document.class).count();
    }

    //TODO FIX this performance issue
    static List<Document> getUrlsContainingWords (List<String> words){
        Query<Document> query = DatabaseDriver.datastore.createQuery(Document.class);
        for (String word : words){
            query.and(query.criteria("words."+word).exists());
        }
        return query.asList();
    }

    static Double getUrlPageRank (Integer urlId){
        return DatabaseDriver.datastore.createQuery(URL.class).field("urlId").equal(urlId).get().getUrlRank();
    }
}
