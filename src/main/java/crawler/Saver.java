package crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.PathGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(PathGenerator.generate("HTML", String.valueOf(url.hashCode())).toFile()))) {

            Map<String, String> data = new HashMap<>();
            Element title = document.select("title").first();
            if (title != null) {
                data.put("title", title.text());
            }
            Element description = document.select("meta[property=og:description]").first();
            if (description != null) {
                data.put("description", description.attr("content"));
            }
            Element body = document.select("body").first();
            if (body != null) {
                data.put("body", body.text().toLowerCase());
            }
            stream.writeObject(data);

            Set<String> links = new HashSet<>();
            for (Element element : document.select("a[href]")) {
                String link = element.absUrl("href");

                if (link.equals("")) {
                    continue;
                }
                links.add(link);
                Main.submitNewLink(link);
            }
            DatabaseController.sendURL(url, links);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Saved " + url);
    }
}
