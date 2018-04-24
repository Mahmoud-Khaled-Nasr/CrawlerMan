package crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.PathGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The Saver class represents a runnable save task for a document.
 */
class Saver implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Saver.class.getName());

    private final Document document;

    /**
     * Constructs an instance of a save task for the given document.
     * @param document The document to be saved
     */
    Saver(Document document) {
        this.document = document;
    }

    @Override
    public void run() {
        String url = document.location();
        LOGGER.info("Saving " + url);
        try (PrintWriter file = new PrintWriter(PathGenerator.generate("HTML", String.valueOf(url.hashCode())).toFile())) {

            Element title = document.select("title").first();
            if (title != null) {
                file.println(title.text());
            } else {
                file.println(url);
            }
            Element description = document.select("meta[property=og:description]").first();
            if (description != null) {
                file.println(description.attr("content"));
            } else {
                file.println(url);
            }
            Element body = document.select("body").first();
            if (body != null) {
                file.println(body.text());
            }

            Set<String> links = new HashSet<>();
            for (Element element : document.select("a[href]")) {
                String link = element.absUrl("href");

                if (link.equals("")) {
                    continue;
                }
                links.add(link);
                Crawler.submitNewLink(link);
            }
            DatabaseController.sendURL(url, links);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Saved " + url);
    }
}
