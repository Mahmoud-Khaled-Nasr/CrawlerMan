package indexer;

import org.bson.Document;
import util.DatabaseDriver;

import java.util.*;

class DatabaseController {

    static void updateDocument(int id, List<String> words){
        //TODO calc the frequency of words in the list
        //TODO update words in the Inverted index collection
        //TODO update the document in the index
    }

    static void insertURL(int id, String URL){
        Map<String, Object> map = new HashMap<>();
        map.put(Integer.toString(id), URL);
        DatabaseDriver.insertRecord(DatabaseDriver.URL_MAP_COLLECTION, map);
    }

    static void recieveURL (String URL, Set<String> links){
        assert DatabaseDriver.collectionExists(DatabaseDriver.COMMUNICATION_COLLECTION);
        if (DatabaseDriver.isCollectionEmpty(DatabaseDriver.COMMUNICATION_COLLECTION)){
            URL = null;
            links = null;
        }else{

            Document document = DatabaseDriver.mongoDatabase.getCollection(DatabaseDriver.COMMUNICATION_COLLECTION)
                    .findOneAndDelete(new Document());
            URL = document.getString(DatabaseDriver.COMMUNICATION_URL);
            //TODO this line needs testing
            links = new HashSet<>((ArrayList<String>) document.get(DatabaseDriver.COMMUNICATION_URL_CHILDREN));
        }
    }


}
