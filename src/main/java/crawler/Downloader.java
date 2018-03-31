package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * The Downloader class represents a callable download task for a URL.
 */
class Downloader implements Callable<Document> {

    private static final Logger LOGGER = Logger.getLogger(Downloader.class.getName());

    private final String url;

    /**
     * Constructs an instance of a download task for the given URL.
     * @param url The URL to be downloaded
     */
    Downloader(String url) {
        this.url = url;
    }

    @Override
    public Document call() throws Exception {
        try {
            LOGGER.info("Downloading " + url);
            return Jsoup.connect(url).get();
        } finally {
            DatabaseController.crawled(url);
            LOGGER.info("Downloaded " + url);
        }
    }
}
