package indexer;

import opennlp.tools.stemmer.PorterStemmer;
import util.PathGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public class Stemmer implements Runnable {

    final private int urlId;

    Stemmer(int urlId) {
        this.urlId = urlId;
    }

    @Override
    public void run() {
        try {
            String html = new String(Files.readAllBytes(PathGenerator.generate("HTML", String.valueOf(urlId))));
            String[] words = html.replaceAll("[^\\p{L} ]", " ").toLowerCase().split("\\s+");
            PorterStemmer porterStemmer = new PorterStemmer();
            List<String> stemmedWords = new LinkedList<>();
            for (String word : words) {
                String stemmedWord = porterStemmer.stem(word);
                if (stemmedWord.length() > 1) {
                    stemmedWords.add(stemmedWord);
                }
            }
            // TODO check if it works
            DatabaseController.updateDocument(urlId, stemmedWords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
