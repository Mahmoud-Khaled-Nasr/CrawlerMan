package crawler;

import model.Channel;
import model.CrawlerState;
import org.mongodb.morphia.query.Query;
import util.DatabaseDriver;

import java.util.*;

class DatabaseController {

    static void loadState(Set<String> URLs, Set<String> visited) {
        List<CrawlerState> crawlerStateList = DatabaseDriver
                .datastore.createQuery(CrawlerState.class).asList();
        for (CrawlerState crawlerState : crawlerStateList){
            if (crawlerState.getUrlState() == CrawlerState.State.PENDING) {
                URLs.add(crawlerState.getUrl());
            }else{
                visited.add(crawlerState.getUrl());
            }
        }
    }

    static void crawling(String URL) {
        DatabaseDriver.saveRecord(new CrawlerState(URL, CrawlerState.State.PENDING));
    }

    static void crawled(String URL) {
        Query<CrawlerState> query = DatabaseDriver.datastore
                .createQuery(CrawlerState.class).field("url").equal(URL);
        List<CrawlerState> crawlerStateList = query.asList();
        if (crawlerStateList.size() == 0){
            DatabaseDriver.saveRecord(new CrawlerState(URL, CrawlerState.State.VISITED));
        }else{
            DatabaseDriver.datastore.update(query
                    , DatabaseDriver.datastore
                            .createUpdateOperations(CrawlerState.class)
                            .set("urlState", CrawlerState.State.VISITED));
        }
    }

    // clear the "crawlerState" collection
    static void clearState() {
        DatabaseDriver.datastore.delete(DatabaseDriver.datastore.createQuery(CrawlerState.class));
    }

    // send the url and its outbound links to the indexer via the "communication" collection
    static void sendURL(String url, Set<String> links) {
        DatabaseDriver.saveRecord(new Channel(url, new ArrayList<>(links)));
    }

    static void closeChannel (){
        DatabaseDriver.saveRecord(new Channel("", new ArrayList<>()));
    }

}
