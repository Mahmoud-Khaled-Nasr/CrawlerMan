package indexer;

import com.mongodb.CursorType;
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
        if (DatabaseDriver.datastore.createQuery(URL.class).field("url").equal(url).asList().isEmpty()) {
            DatabaseDriver.saveRecord(new URL(id, url));
        }
    }

    private static Iterator<Channel> channelIterator = null;

    static String receiveURL (Set<String> links){

        if(channelIterator == null) {
            channelIterator = DatabaseDriver.datastore.createQuery(Channel.class)
                    .fetch(new FindOptions()
                            .cursorType(CursorType.Tailable));
            Channel channel = channelIterator.next();
            assert channel.getURL().equals("");
        }
        Channel channel = channelIterator.next();
        links.addAll(channel.getChildren());
        return channel.getURL();
    }

    //TODO clear the channel
}
