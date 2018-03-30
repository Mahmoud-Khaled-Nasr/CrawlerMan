package util;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import model.Channel;
import model.CrawlerState;
import org.bson.Document;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.MapperOptions;
import org.mongodb.morphia.query.Query;

import java.util.*;

public class DatabaseDriver {
    public static MongoDatabase mongoDatabase;
    private static final String DB_NAME = "CrawlerManDB2";
    private static final String MODEL_PACKAGE = "model";
    public static final String COMMUNICATION_COLLECTION = "communication"
            , COMMUNICATION_URL = "URL"
            , COMMUNICATION_URL_CHILDREN = "children";
    public static final String GRAPH_COLLECTION = "graph"
            , GRAPH_URL = "URL"
            , GRAPH_URL_CHILDREN = "children"
            , GRAPH_URL_PARENTS = "parents";
    public static final String STATE_COLLECTION = "crawlerState"
            , STATE_URL = "URL"
            , STATE_URL_STATE = "state"
            , STATE_PENDING = "pending"
            , STATE_DONE = "done";
    public static final String URL_MAP_COLLECTION = "URLMap"
            , URL_MAP_URL = "URL"
            , URL_MAP_ID = "id"
            , URL_MAP_RANK = "rank";
    public static final String INDEX_COLLECTION = "index"
            , INDEX_URL_ID = "id"
            , INDEX_WORDS = "words";
    public static final String INVERTED_INDEX_COLLECTION = "invertedIndex"
            , INVERTED_INDEX_WORD = "word"
            , INVERTED_INDEX_URLS = "URLS"
            , INVERTED_INDEX_URLS_ID = "id"
            , INVERTED_INDEX_URLS_TF = "TF";

    public static void initializeDatabase(){
        MongoClient mongoClient = new MongoClient();
        mongoDatabase = mongoClient.getDatabase(DB_NAME);

        final Morphia morphia = new Morphia();

        morphia.mapPackage(MODEL_PACKAGE);

        // create the Datastore connecting to the default port on the local host
        final Datastore datastore = morphia.createDatastore(new MongoClient(), DB_NAME);
        MapperOptions mapperOptions = new MapperOptions();
        mapperOptions.setStoreEmpties(true);
        datastore.ensureIndexes();


        Channel channel = new Channel();
        channel.URL="test";
        channel.children=new ArrayList<>(Arrays.asList("test child",""));
        CrawlerState state = new CrawlerState("url", CrawlerState.State.PENDING);
        datastore.save(state);
        /*channel = new Channel();
        Query<Channel> query = datastore.createQuery(Channel.class);
        List<Channel> channels = query.asList();
        for (Channel c : channels) {
            System.out.println(c.URL);
        }*/
    }
}
