package crawler;

import org.bson.Document;
import util.DatabaseDriver;

import java.util.*;

class DatabaseController {

    //TODO the URLs shouldn't be a set !!!???
    static void loadState(Set<String> URLs, Set<String> visited) {
        if (! DatabaseDriver.isCollectionEmpty(DatabaseDriver.STATE_COLLECTION)){
            for (Document document : DatabaseDriver.getAllRecords(DatabaseDriver.STATE_COLLECTION)){
                String state = document.getString(DatabaseDriver.STATE_URL_STATE);
                if (state.equals(DatabaseDriver.STATE_PENDING)){
                    URLs.add(state);
                }else{
                    visited.add(state);
                }
            }
        }
    }

    static void crawling(String URL) {
        // TODO add <URL, state> record to the "crawlerState" collection with state="pending"

    }

    static void crawled(String URL) {
        // TODO add/update <URL, state> record to the "crawlerState" collection with state="done"
    }

    // clear the "crawlerState" collection
    static void clearState() {
        DatabaseDriver.deleteAllRecords(DatabaseDriver.STATE_COLLECTION);
    }

    // send the url and its outbound links to the indexer via the "communication" collection
    static void sendURL(String url, Set<String> links) {
        Map<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<>(links);
        map.put(url, list);
        DatabaseDriver.insertRecord(DatabaseDriver.COMMUNICATION_COLLECTION, map);
    }

}
