package web;

import model.URL;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import search_engine.SearchEngine;
import util.PathGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@RestController
public class SearchController {

    final static private Set<String> suggestions = new TreeSet<>();

    @GetMapping("/suggestions")
    public Set<String> getSuggestions() {
        return suggestions;
    }

    @PostMapping("/search")
    public Map<String, Object> search(@RequestParam(value = "query") String query, @RequestParam(value = "page", defaultValue="0") int page) throws InterruptedException, IOException {
        suggestions.add(query);
        if(page > 0) {
            page--;
        }
        final int PAGE_SIZE = 10;
        Map<String, Object> response = new TreeMap<>();

        List<URL> urls = SearchEngine.search(query);
        response.put("pages", urls.size() / PAGE_SIZE + ((urls.size() % PAGE_SIZE > 0) ? 1 : 0));

        List<Result> results = new LinkedList<>();
        for (int i = page * PAGE_SIZE; i < urls.size() && i < page * PAGE_SIZE + PAGE_SIZE; i++) {
            results.add(getResultFromURL(urls.get(i)));
        }
        response.put("results", results);

        return response;
    }

    private static Result getResultFromURL (URL url) throws IOException {
        try (BufferedReader file = new BufferedReader(new FileReader(PathGenerator.generate("HTML", String.valueOf(url.getUrlId())).toFile()))) {
            String title = file.readLine();
            String snippet = url.getURL();
            return new Result(title, url.getURL(), snippet);
        }
    }
}
