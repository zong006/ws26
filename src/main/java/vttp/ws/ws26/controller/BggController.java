package vttp.ws.ws26.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PutMapping("/review/{reviewId}")
    public ResponseEntity<Boolean> updateReview(@PathVariable String reviewId, @RequestBody Document update){

        boolean reviewExists = bggService.updateReview(reviewId, update);

        return ResponseEntity.ok().body(reviewExists);
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<Document> getLatestReview(@PathVariable String reviewId) throws Exception{
        Document result = bggService.getLatestReview(reviewId);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/review/{reviewId}/history")
    public ResponseEntity<Document> getReviewHistory(String reviewId) throws Exception{
     Document result = bggService.getReviewHistory(reviewId);
     return ResponseEntity.ok().body(result);
    }

    @GetMapping("/review/get")
    public ResponseEntity<List<Document>> getAllReviews() throws Exception{
        List<Document> reviews = bggService.getAllReviews();
        return ResponseEntity.ok().body(reviews);
    }


    @GetMapping("/{gameId}/reviews")
    public ResponseEntity<Document> getGameWithComments(@PathVariable int gameId) throws NumberFormatException, Exception{
        List<Document> results = bggService.getGameWithComments(gameId);
        if (results.size()==1){
            // System.out.println("size: " + opt.get().size());
            return ResponseEntity.ok().body(results.getFirst());
        }

        Document error = new Document();
        error.put("message", "Invalid game id..");
        return ResponseEntity.badRequest().body(error);
    }

    @GetMapping("/{highOrLow}")
    public ResponseEntity<List<Document>> getGamesWithHighestOrLowestRating(@PathVariable String highOrLow){
        boolean fromHighest;
        if (highOrLow.toLowerCase().equals("highest")){
            fromHighest = true;
        }
        else if (highOrLow.toLowerCase().equals("lowest")){
            fromHighest = false;
        }
        else {
            Document error = new Document();
            error.put("message", "indicate either highest or lowest..");
            return ResponseEntity.badRequest().body(Arrays.asList(error));
        }

        List<Document> results = bggService.getGamesWithHighestOrLowestRating(fromHighest);

        return ResponseEntity.ok().body(results);
    }
}
