package vttp.ws.ws26.controller;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vttp.ws.ws26.service.BggService;

@RestController
@RequestMapping(path = "/games", produces = "application/json")
public class BggController {
    
    @Autowired
    private BggService bggService;

    @GetMapping()
    public ResponseEntity<Document> browseGames(@RequestParam(name = "limit", defaultValue = "25") int limit,
                                                        @RequestParam(name = "offset", defaultValue = "0") int offset
                                                        ){

        Document queryResults = bggService.browseGames(limit, offset);

        return ResponseEntity.ok().body(queryResults);
    }

    @GetMapping("/rank")
    public ResponseEntity<Document> browseGamesByRank(@RequestParam(name = "limit", defaultValue = "25") int limit,
                                                        @RequestParam(name = "offset", defaultValue = "0") int offset){
        Document queryResult = bggService.browseGamesByRanking(limit, offset);
        return ResponseEntity.ok().body(queryResult);
    }

    @GetMapping
    public ResponseEntity<Document> getOneGame(){
        return ResponseEntity.ok().body(bggService.getOneGame());
    }


}
