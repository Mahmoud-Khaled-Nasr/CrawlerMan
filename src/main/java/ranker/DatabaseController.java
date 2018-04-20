package ranker;

import model.Node;
import model.URL;
import util.DatabaseDriver;

import java.util.List;
import java.util.Map;

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
}
