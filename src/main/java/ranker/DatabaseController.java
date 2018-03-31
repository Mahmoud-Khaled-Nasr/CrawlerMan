package ranker;

import model.URL;
import util.DatabaseDriver;

import java.util.Map;

class DatabaseController {

    static void updatePageRanks(Map<Integer, Double> ranks){
        for (int urlId : ranks.keySet()) {
            URL url = DatabaseDriver.datastore.createQuery(URL.class).field("urlId").equal(urlId).get();
            url.setUrlRank(ranks.get(urlId));
            DatabaseDriver.saveRecord(url);
        }
    }
}
