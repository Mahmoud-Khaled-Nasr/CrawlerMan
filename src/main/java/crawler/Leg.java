package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.Pair;
import util.PathGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class Leg implements Runnable {

    private BlockingQueue<String> URLs;
    private BlockingQueue<Pair<String, Set<String>>> candidateURLs;

    Leg(BlockingQueue<String> URLs, BlockingQueue<Pair<String, Set<String>>> candidateURLs) {
        this.URLs = URLs;
        this.candidateURLs = candidateURLs;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String url = URLs.take();
                if (url.equals("")) {
                    break;
                }
                try (PrintWriter file = new PrintWriter(PathGenerator.generate("HTML", String.valueOf(url.hashCode())).toString())) {
                    Document content = Jsoup.connect(url).get();
                    file.println(content);

                    Set<String> candidateURLsSet = new HashSet<>();
                    for (Element element : content.select("a[href]")) {
                        candidateURLsSet.add(element.absUrl("href"));
                    }
                    candidateURLs.put(new Pair<>(url, candidateURLsSet));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            candidateURLs.put(new Pair<>("", null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
