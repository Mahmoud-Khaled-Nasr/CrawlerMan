package util;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import model.Channel;
import org.bson.Document;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.MapperOptions;
import org.mongodb.morphia.query.FindOptions;

import java.util.*;

public class DatabaseDriver {
    private static final String DB_NAME = "CrawlerManDB2";
    private static final String MODEL_PACKAGE = "model";

    public static Datastore datastore;

    public static void initializeDatabase(){
        ;

        final Morphia morphia = new Morphia();

        morphia.mapPackage(MODEL_PACKAGE);

        // create the Datastore connecting to the default port on the local host
        datastore = morphia.createDatastore(new MongoClient(), DB_NAME);
        //TODO check if the mapper option is applied
        MapperOptions mapperOptions = morphia.getMapper().getOptions();
        mapperOptions.setStoreEmpties(true);
        datastore.ensureIndexes();
    }

    public static <T> void saveRecord (T entity){
        datastore.save(entity);
    }

    public static <T> List<T> getRecords(Class<T> classType, int limit){
        return datastore.createQuery(classType).asList(new FindOptions().limit(limit));
    }
}
