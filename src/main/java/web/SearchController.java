package web;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import search_engine.Result;
import search_engine.SearchEngine;

import java.util.Collection;

@RestController
public class SearchController {

    @PostMapping("/search")
    public Collection<Result> search(@RequestParam(value = "query") String query, @RequestParam(value = "page", defaultValue="0") int page) throws InterruptedException {
        return SearchEngine.search(query, page);
    }
}
