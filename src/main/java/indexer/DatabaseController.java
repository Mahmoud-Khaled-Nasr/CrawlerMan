package indexer;

import model.Channel;
import model.URL;
import org.mongodb.morphia.query.FindOptions;
import util.DatabaseDriver;

import java.util.*;

class DatabaseController {

    static void updateDocument(int id, List<String> words){
        //TODO calc the frequency of words in the list
        //TODO update words in the Inverted index collection
        //TODO update the document in the index

    }

    static void insertURL(int id, String url){
        DatabaseDriver.saveRecord(new URL(id, url, 1));
    }

    static String receiveURL (Set<String> links){
        List<Channel> channels = DatabaseDriver.datastore.createQuery(Channel.class)
                .asList(new FindOptions().limit(1));
        Channel channel = channels.get(0);
        DatabaseDriver.datastore.delete(channel);
        links.addAll(channel.getChildren());
        return channel.getURL();
    }
}
