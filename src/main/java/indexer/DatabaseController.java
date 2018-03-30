package indexer;

import com.mongodb.CursorType;
import model.Channel;
import model.URL;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import util.DatabaseDriver;

import java.util.*;

class DatabaseController {

    static void updateDocument(int id, List<String> words){
        //TODO calc the frequency of words in the list
        //TODO update words in the Inverted index collection
        //TODO update the document in the index

    }

    static void insertURL(int id, String url){
        if (DatabaseDriver.datastore.createQuery(URL.class).field("url").equal(url).asList().isEmpty()) {
            DatabaseDriver.saveRecord(new URL(id, url));
        }
    }

    static String receiveURL (Set<String> links){
        while (DatabaseDriver.datastore.createQuery(Channel.class).count() == 0) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Channel channel = DatabaseDriver.datastore.createQuery(Channel.class).get();
        DatabaseDriver.datastore.delete(channel);
        if (channel.getURL().equals("")
                && DatabaseDriver.datastore.createQuery(Channel.class).count() == 0){
            Channel tempChannel = DatabaseDriver.datastore.createQuery(Channel.class).get();
            DatabaseDriver.datastore.delete(tempChannel);
            DatabaseDriver.saveRecord(channel);
            channel = tempChannel;
        }
        links.addAll(channel.getChildren());
        return channel.getURL();
    }

    //TODO clear the channel
}
