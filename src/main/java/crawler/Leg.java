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
    private RobotMonitor robotMonitor;

    Leg(BlockingQueue<String> URLs, BlockingQueue<Pair<String, Set<String>>> candidateURLs, RobotMonitor robotMonitor) {
        this.URLs = URLs;
        this.candidateURLs = candidateURLs;
        this.robotMonitor = robotMonitor;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String url = URLs.take();
                if (url.equals("")) {
                    break;
                }
                try (PrintWriter file = new PrintWriter(PathGenerator.generate("HTML", String.valueOf(url.hashCode())).toFile())) {
                    Document content = Jsoup.connect(url).get();
                    Element title = content.select("title").first();
                    if (title != null) {
                        file.println(title.text());
                    }
                    Element body = content.select("body").first();
                    if (body != null) {
                        file.println(body.text());
                    }

                    Set<String> candidateURLsSet = new HashSet<>();
                    for (Element element : content.select("a[href]")) {
                        String candidateURL = element.absUrl("href");

                        if (candidateURL.equals("")){
                            continue;
                        }
                        if (robotMonitor.isAllowed(candidateURL)){
                            candidateURLsSet.add(candidateURL);
                        }
                    }
                    //TODO check if the candidateURLsSet is empty
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
