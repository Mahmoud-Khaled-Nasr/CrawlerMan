package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

class Downloader implements Callable<Document> {

    private static final Logger LOGGER = Logger.getLogger(Downloader.class.getName());

    private final String url;

    Downloader(String url) {
        this.url = url;
    }

    @Override
    public Document call() throws Exception {
        try {
            LOGGER.log(Level.INFO,"Downloading " + url);
            return Jsoup.connect(url).get();
        } finally {
            DatabaseController.crawled(url);
            LOGGER.log(Level.INFO,"Downloaded " + url);
        }
    }
}
