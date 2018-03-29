package util;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.util.*;

public class DatabaseDriver {
    public static MongoDatabase mongoDatabase;
    private static final String DB_NAME = "CrawlerManDB";
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

        if (! collectionExists(COMMUNICATION_COLLECTION)) {
            createCommunicationCollection();
        }
        if (! collectionExists(GRAPH_COLLECTION)){
            createGraphCollection();
        }
        if (! collectionExists(STATE_COLLECTION)){
            createStateCollection();
        }
        if (! collectionExists(URL_MAP_COLLECTION)){
            createURLMapCollection();
        }
        if (! collectionExists(INDEX_COLLECTION)){
            createIndexCollection();
        }
        if (! collectionExists(INVERTED_INDEX_COLLECTION)){
            createInvertedIndexCollection();
        }

        //TODO check if the client needs to be closed
        //mongoClient.close();
    }

    public static void insertRecord (String collection, Map<String, Object> input){
        assert collectionExists(collection);
        Document document = new Document(input);
        mongoDatabase.getCollection(collection).insertOne(document);
    }

    public static void insertRecords (String collection, List<Map<String, Object>> inputs){
        assert collectionExists(collection);
        List<Document> records = new ArrayList<>();
        for (Map<String, Object> input : inputs){
            records.add(new Document(input));
        }
        mongoDatabase.getCollection(collection).insertMany(records);

    }

    public static void dropCollection (String collection){
        assert collectionExists(collection);
        mongoDatabase.getCollection(collection).drop();
    }

    public static void deleteAllRecords (String collection){
        assert collectionExists(collection);
        mongoDatabase.getCollection(collection).deleteMany(new Document());
    }

    public static List<Document> getAllRecords (String collection){
        assert collectionExists(collection);
        List<Document>list = new ArrayList<>();
        for (Document document : mongoDatabase.getCollection(collection).find()){
            list.add(document);
        }
        return list;
    }

    public static boolean isCollectionEmpty(String collection){
        assert collectionExists(collection);
        FindIterable<Document> documents = mongoDatabase.getCollection(collection).find();
        for (Document document : documents){
            if (document.getObjectId("_id") == null){
                return true;
            }
        }
        return false;
    }

    private static void createCommunicationCollection() {
        Document connectionValidator = new Document();
        List<String> requiredList = new ArrayList<>(Arrays.asList(COMMUNICATION_URL, COMMUNICATION_URL_CHILDREN));
        connectionValidator
                .append("bsonType", "object")
                .append("required", requiredList)
                .append("additionalProperties", false)
                .append("properties", new Document()
                        .append("_id", new Document())
                        .append(COMMUNICATION_URL, new Document()
                                .append("bsonType", "string")
                                .append("description", "The crawled URL"))
                        .append(COMMUNICATION_URL_CHILDREN, new Document()
                                .append("bsonType", "array")
                                .append("description", "The children of the crawled URL")));

        createCollection(COMMUNICATION_COLLECTION, connectionValidator);
    }

    //TODO delete this collection
    private static void createGraphCollection() {
        // TODO add parent array
        Document graphValidator = new Document();
        List<String> requiredList = new ArrayList<>(Arrays.asList(GRAPH_URL, GRAPH_URL_CHILDREN));
        graphValidator
                .append("bsonType", "object")
                .append("required", requiredList)
                .append("additionalProperties", false)
                .append("properties", new Document()
                        .append("_id", new Document())
                        .append(GRAPH_URL, new Document()
                                .append("bsonType", "string")
                                .append("description", "The URL"))
                        .append(GRAPH_URL_CHILDREN, new Document()
                                .append("bsonType", "array")
                                .append("description", "The children of the crawled URL"))
                        .append(GRAPH_URL_PARENTS, new Document()
                                .append("bsonType", "array")
                                .append("description", "The parents of the crawled URL")));


        createCollection(GRAPH_COLLECTION, graphValidator);
    }

    private static void createStateCollection(){
        Document stateValidator = new Document();
        List<String> requiredList = new ArrayList<>(Arrays.asList(STATE_URL, STATE_URL_STATE));
        stateValidator
                .append("bsonType", "object")
                .append("required", requiredList)
                .append("additionalProperties", false)
                .append("properties", new Document()
                        .append("_id", new Document())
                        .append(STATE_URL, new Document()
                                .append("bsonType", "string"))
                        .append(STATE_URL_STATE, new Document()
                                //TODO validate the enum
                                .append("bsonType", "enum")));

       createCollection(STATE_COLLECTION, stateValidator);
    }

    private static void createURLMapCollection(){
        Document URLMapValidator = new Document();
        List<String> requiredList = new ArrayList<>(Arrays.asList(URL_MAP_ID, URL_MAP_URL, URL_MAP_RANK));
        URLMapValidator
                .append("bsonType", "object")
                .append("required", requiredList)
                .append("additionalProperties", false)
                .append("properties", new Document()
                        .append("_id", new Document())
                        .append(URL_MAP_ID, new Document()
                                .append("bsonType", "int")
                                .append("description", "The hash id of the URL"))
                        .append(URL_MAP_URL, new Document()
                                .append("bsonType", "string"))
                        .append(URL_MAP_RANK, new Document()
                                .append("bsonType", "int")));

        createCollection(URL_MAP_COLLECTION, URLMapValidator);

        mongoDatabase.getCollection(URL_MAP_COLLECTION)
                .createIndex(new Document().append(URL_MAP_URL, 1)
                        , new IndexOptions()
                                .unique(true)
                                .name(URL_MAP_URL));
    }

    private static void createIndexCollection(){
        Document indexValidator = new Document();
        List<String> requiredList = new ArrayList<>(Arrays.asList(INDEX_URL_ID, INDEX_WORDS));
        indexValidator
                .append("bsonType", "object")
                .append("required", requiredList)
                .append("additionalProperties", false)
                .append("properties", new Document()
                        .append("_id", new Document())
                        .append(INDEX_URL_ID, new Document()
                                .append("bsonType", "int"))
                        .append(INDEX_WORDS, new Document()
                                .append("bsonType", "array")));
        createCollection(INDEX_COLLECTION, indexValidator);
    }

    private static void createInvertedIndexCollection (){
        Document invertedIndexValidator = new Document();
        List<String> requiredList = new ArrayList<>(Arrays.asList(INVERTED_INDEX_WORD, INVERTED_INDEX_URLS));
        invertedIndexValidator
                .append("bsonType", "object")
                .append("required", requiredList)
                .append("additionalProperties", false)
                .append("properties", new Document()
                        .append("_id", new Document())
                        .append(INVERTED_INDEX_WORD, new Document()
                                .append("bsonType", "text"))
                        .append(INVERTED_INDEX_URLS, new Document()
                                .append("bsonType", "array")));

        createCollection(INVERTED_INDEX_COLLECTION, invertedIndexValidator);

        mongoDatabase.getCollection(INVERTED_INDEX_COLLECTION)
                .createIndex(new Document().append(INVERTED_INDEX_WORD, 1)
                        , new IndexOptions()
                                .unique(true)
                                .name(INVERTED_INDEX_WORD));
    }

    private static void createCollection(String collectionName, Document validator){
        ValidationLevel validationLevel = ValidationLevel.STRICT;
        ValidationAction validationAction = ValidationAction.WARN;
        mongoDatabase.createCollection(collectionName
                , new CreateCollectionOptions()
                        .validationOptions(new ValidationOptions()
                                .validationAction(validationAction)
                                .validationLevel(validationLevel)
                                .validator(validator)));
    }


    public static boolean collectionExists(final String collection){
        for (String s : mongoDatabase.listCollectionNames()){
            if (s.equals(collection)){
                return true;
            }
        }
        return false;
    }
}
