package search_engine;

import model.Document;
import model.URL;
import util.DatabaseDriver;

import java.util.List;

public class DatabaseController {

    static List<URL> getUrls (List<Integer> urls){
        return DatabaseDriver.datastore.createQuery(URL.class).field("urlId").in(urls).asList();
    }
}
