package util;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DatabaseDriver {
    private static MongoDatabase mongoDatabase;
    private static final String DB_NAME = "CrawlerManDB";
    private static final String COMMUNICATION_COLLECTION = "communication"
            , COMMUNICATION_URL = "URL"
            , COMMUNICATION_URL_CHILDREN = "children";
    private static final String GRAPH_COLLECTION = "communication"
            , GRAPH_URL = "URL"
            , GRAPH_URL_CHILDREN = "children";
    private static final String STATE_COLLECTION = "crawlerState"
            , STATE_VISITED = "visited"
            , STATE_QUEUE = "queue";

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

        mongoClient.close();
    }

    public static void insertRecord (String collection, Map<String, Object> input){
        assert collectionExists(collection);
        mongoDatabase.getCollection(collection).insertOne(new Document(input));
    }

    public static void insertRecords (String collection, List<Map<String, Object>> inputs){
        assert collectionExists(collection);
        List<Document> records = new ArrayList<>();
        for (Map<String, Object> input : inputs){
            records.add(new Document(input));
        }
        mongoDatabase.getCollection(collection).insertMany(records);

    }

    private static void createCommunicationCollection() {
        Document connectionValidator = new Document();
        List<String> requiredList = new ArrayList<>();
        requiredList.add(COMMUNICATION_URL);
        requiredList.add(COMMUNICATION_URL_CHILDREN);
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

        mongoDatabase.createCollection(COMMUNICATION_COLLECTION,
                new CreateCollectionOptions()
                        .autoIndex(true)
                        .validationOptions(new ValidationOptions()
                                .validationAction(ValidationAction.ERROR)
                                .validationLevel(ValidationLevel.STRICT)
                                .validator(connectionValidator)));

    }

    //TODO delete this collection
    private static void createGraphCollection() {
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
                                .append("description", "The children of the crawled URL")));

        mongoDatabase.createCollection(GRAPH_COLLECTION,
                new CreateCollectionOptions()
                        .autoIndex(true)
                        .validationOptions(new ValidationOptions()
                                .validationAction(ValidationAction.ERROR)
                                .validationLevel(ValidationLevel.STRICT)
                                .validator(graphValidator)));
    }

    private static void createStateCollection(){
        Document stateValidator = new Document();
        List<String> requiredList = new ArrayList<>(Arrays.asList(STATE_VISITED, STATE_QUEUE));
        stateValidator
                .append("bsonType", "object")
                .append("required", requiredList)
                .append("additionalProperties", false)
                .append("properties", new Document()
                        .append("_id", new Document())
                        .append(STATE_QUEUE, new Document()
                                .append("bsonType", "array")
                                .append("description", "The unfetched URL"))
                        .append(STATE_VISITED, new Document()
                                .append("bsonType", "array")
                                .append("description", "The crawled URL")));

        mongoDatabase.createCollection(STATE_COLLECTION,
                new CreateCollectionOptions()
                        .autoIndex(true)
                        .validationOptions(new ValidationOptions()
                                .validationAction(ValidationAction.ERROR)
                                .validationLevel(ValidationLevel.STRICT)
                                .validator(stateValidator)));
    }


    private static boolean collectionExists(final String collection){
        for (String s : mongoDatabase.listCollectionNames()){
            if (s.equals(collection)){
                return true;
            }
        }
        return false;
    }
}
