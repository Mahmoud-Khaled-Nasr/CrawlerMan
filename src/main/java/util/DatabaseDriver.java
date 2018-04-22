package util;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.MapperOptions;

public class DatabaseDriver {
    private static final String DB_NAME = "CrawlerManDB2";
    private static final String MODEL_PACKAGE = "model";

    public static Datastore datastore = null;

    public static void initializeDatabase() {

        if (datastore != null) {
            return;
        }

        final Morphia morphia = new Morphia();

        morphia.mapPackage(MODEL_PACKAGE);

        // create the Datastore connecting to the default port on the local host
        datastore = morphia.createDatastore(new MongoClient(), DB_NAME);

        MapperOptions mapperOptions = morphia.getMapper().getOptions();
        mapperOptions.setStoreEmpties(true);
        mapperOptions.setStoreNulls(true);
        datastore.ensureIndexes();
    }

    public static <T> void saveRecord (T entity){
        datastore.save(entity);
    }
}
