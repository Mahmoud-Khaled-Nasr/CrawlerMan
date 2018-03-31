package ranker;

import model.URL;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import util.DatabaseDriver;

import java.util.List;
import java.util.Map;

class DatabaseController {

    static void updatePageRanks(Map<Integer, Double> ranks){
        List<URL> urls = DatabaseDriver.datastore.createQuery(URL.class).field("urlId").in(ranks.keySet()).asList();
        //List<URL> urls = DatabaseDriver.datastore.createQuery(URL.class).field("urlId").in(ranks.keySet())
                //.project("urlId", true).project("url", false).asList();
        for (URL url : urls){
            url.setUrlRank(ranks.get(url.getUrlId()));
        }
        DatabaseDriver.datastore.save(urls);
    }
}
