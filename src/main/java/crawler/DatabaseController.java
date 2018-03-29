package crawler;

import java.util.Set;

class DatabaseController {

    static void loadState(Set<String> URLs, Set<String> visited) {
        // TODO if exists, load the state form the database.
        // TODO if not, initialize the "crawlerState" collection
    }

    static void crawling(String URL) {
        // TODO add <URL, state> record to the "crawlerState" collection with state="pending"
    }

    static void crawled(String URL) {
        // TODO add/update <URL, state> record to the "crawlerState" collection with state="done"
    }

    static void clearState() {
        // TODO drop the "crawlerState" collection
    }

    static void sendURL(String url, Set<String> links) {
        // TODO send the url and its outbound links to the indexer via the "communication" collection
    }

}
