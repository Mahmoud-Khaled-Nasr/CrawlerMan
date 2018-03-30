package crawler;

import org.bson.Document;
import util.DatabaseDriver;

import java.util.*;

class DatabaseController {

    //TODO the URLs shouldn't be a set !!!???
    static void loadState(Set<String> URLs, Set<String> visited) {

    }

    static void crawling(String URL) {
        // TODO add <URL, state> record to the "crawlerState" collection with state="pending"

    }

    static void crawled(String URL) {
        // TODO add/update <URL, state> record to the "crawlerState" collection with state="done"
    }

    // clear the "crawlerState" collection
    static void clearState() {

    }

    // send the url and its outbound links to the indexer via the "communication" collection
    static void sendURL(String url, Set<String> links) {

    }

}
