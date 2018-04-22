package search_engine;

import model.URL;
import util.DatabaseDriver;

import java.util.LinkedList;
import java.util.List;

public class DatabaseController {

    static List<URL> getUrls (Iterable<Integer> urls){
        List<URL> urlList = DatabaseDriver.datastore.createQuery(URL.class).field("urlId").in(urls).asList();
        List<URL> sortedUrlList = new LinkedList<>();
        for (Integer urlId : urls){
            for (URL url : urlList) {
                if (urlId.equals(url.getUrlId())){
                    sortedUrlList.add(url);
                }
            }
        }
        return sortedUrlList;
    }
}
