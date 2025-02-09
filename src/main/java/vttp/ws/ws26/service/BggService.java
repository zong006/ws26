package vttp.ws.ws26.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.ws.ws26.model.Game;
import vttp.ws.ws26.model.ReviewForm;
import vttp.ws.ws26.repo.BggRepo;

@Service
public class BggService {

    @Autowired
    private BggRepo bggRepo;

    public Document browseGames(int limit, int offset){
        List<Document> results = bggRepo.getGames(limit, offset);
        System.out.println(results.size());
        Document queryResults = formatResult(results,limit, offset);
        return queryResults;
    }

    public Document browseGamesByRanking(int limit, int offset){
        List<Document> results = bggRepo.getGamesByRank(limit, offset);
        Document queryResults = formatResult(results,limit, offset);
        return queryResults;
    }

    public List<Document> getGameById(Integer gameId){
        List<Document> results = bggRepo.getGameById(gameId);
        return results;
    }

    public ObjectId createNewReview(ReviewForm form) throws ParseException{
        int gid = form.getGid();
        String name = getGameById(gid).getFirst().getString("name");
        
        Document review = new Document();
        
        review.put("review_id", UUID.randomUUID().toString().substring(0, 8));
        review.put("user", form.getUser());
        review.put("rating", form.getRating());
        review.put("comment", form.getComment());
        review.put("posted", new Date());
        review.put("gid", gid);
        review.put("name", name);

        ObjectId id = bggRepo.insertComment(review);

        return id;
    }

    public boolean updateReview(String reviewId, Document updates){
        List<Document> reviews = bggRepo.getReviewById(reviewId);
        if (reviews.size()==1){
            updates.put("timestamp", new Date());
            long updateCount = bggRepo.updateReview(reviewId, updates);
            return updateCount==1;
        }
        return false;
    }

    private Document formatResult(List<Document> results, int limit, int offset){

        List<Game> games = new LinkedList<>();
        for (Document d : results){
            int gameId = d.getInteger("gid");
            String name = d.getString("name");
            Game g = new Game(gameId, name);
            games.add(g);

            
        }

        long timestamp = new Date().getTime();

        Document queryResult = new Document();
        queryResult.put("games", games);
        queryResult.put("offset", offset);
        queryResult.put("limit", limit);
        queryResult.put("total", results.size());
        queryResult.put("timestamp", timestamp);
        return queryResult;
    }   

    public Document getLatestReview(String reviewId) throws Exception{
        Optional<Document> opt =  bggRepo.showLatestReview(reviewId);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String dateString = sdf.format(date);

        if (opt.isPresent()){
            Document result = opt.get();
        
            if (!result.containsKey("edited")){
                result.put("edited", false);
            }

            result.put("timestamp", dateString);
            return result;
        }
        throw new Exception("Review id does not exist..");
    }

    public Document getReviewHistory(String reviewId) throws Exception{
        Optional<Document> opt = bggRepo.getReviewHistory(reviewId);
        if (opt.isPresent()){
            return opt.get();
        }
        
        throw new Exception("Review id does not exist..");
    }

    public List<Document> getAllReviews() throws Exception{
        Optional<List<Document>> opt = bggRepo.getAllReviews();
        if (opt.isPresent()){
            return opt.get();
        }
        throw new Exception("Unable to retrieve documents..");
    }

}
