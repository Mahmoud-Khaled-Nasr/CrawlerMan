package web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import search_engine.SearchEngine;

import java.util.Collection;

@RestController
public class SearchController {

    @RequestMapping("/search")
    public Collection<Integer> search(@RequestParam("query") String query) throws InterruptedException {
        return SearchEngine.search(query);
    }
}
