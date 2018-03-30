package indexer;

import org.bson.Document;
import util.DatabaseDriver;

import java.util.*;

class DatabaseController {

    static void updateDocument(int id, List<String> words){
        //TODO calc the frequency of words in the list
        //TODO update words in the Inverted index collection
        //TODO update the document in the index
    }

    static void insertURL(int id, String URL){

    }

    static String recieveURL (Set<String> links){
        return "";
    }
}
