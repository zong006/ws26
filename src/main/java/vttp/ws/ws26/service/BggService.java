package vttp.ws.ws26.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.ws.ws26.model.Game;
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

    public Document getGameById(int gameId){
        List<Document> results = bggRepo.getGameById(gameId);
        return results.getFirst();
    }

    public Document getOneGame(){
        return bggRepo.getOneComment().getFirst();
    }

    private Document formatResult(List<Document> results, int limit, int offset){

        
        Document result = results.getFirst();
        ObjectId id = (ObjectId) result.get("_id");
        Long timestamp = (long) id.getTimestamp();
        // System.out.println(timestamp);
        // Date date = new Date(timestamp*1000);
        // System.out.println(date);

        List<Game> games = new LinkedList<>();
        for (Document d : results){
            int gameId = d.getInteger("gid");
            String name = d.getString("name");
            Game g = new Game(gameId, name);
            games.add(g);

            
        }

        Document queryResult = new Document();
        queryResult.put("games", games);
        queryResult.put("offset", offset);
        queryResult.put("limit", limit);
        queryResult.put("total", results.size());
        queryResult.put("timestamp", timestamp);
        return queryResult;
    }   

}
