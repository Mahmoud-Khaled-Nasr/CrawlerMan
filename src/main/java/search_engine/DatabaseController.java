package search_engine;

import model.Document;
import util.DatabaseDriver;

import java.util.List;

public class DatabaseController {

    static List<Document> getDocuments (List<Integer> urlIds){
        return DatabaseDriver.datastore.createQuery(Document.class).field("urlId").in(urlIds).asList();
    }
}
