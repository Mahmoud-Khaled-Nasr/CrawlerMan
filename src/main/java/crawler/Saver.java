package crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.PathGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class Saver implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Saver.class.getName());

    private final Document document;

    Saver(Document document) {
        this.document = document;
    }

    @Override
    public void run() {
        String url = document.location();
        LOGGER.log(Level.INFO,"Saving " + url);
        try (PrintWriter file = new PrintWriter(PathGenerator.generate("HTML", String.valueOf(url.hashCode())).toFile())) {

            Element title = document.select("title").first();
            if (title != null) {
                file.println(title.text());
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
                Main.submitNewLink(link);
            }
            DatabaseController.sendURL(url, links);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO,"Saved " + url);
    }
}