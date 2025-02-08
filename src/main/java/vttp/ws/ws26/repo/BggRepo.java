package vttp.ws.ws26.repo;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class BggRepo {
    
    @Autowired
    private MongoTemplate mongoTemplate;


    /*
        db.game.find({}).limit("number to limit").skip("number to offset")
        db.game.find({}).count()
    */ 
    public List<Document> getGames(int limit, int offset){
        Criteria criteria = Criteria.where(null);
        Query query = Query.query(criteria).limit(limit).skip(offset);
        List<Document> results = mongoTemplate.find(query, Document.class, MongoConstants.MONGO_BGG_C_NAME);

        return results;
    }

    /*
        db.game.find({}).sort({"ranking":1}).limit("number to limit").skip("number to offset")
    */ 

    public List<Document> getGamesByRank(int limit, int offset){

        Criteria criteria = Criteria.where(null);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.ASC, MongoConstants.MONGO_BGG_RANKING)).limit(limit).skip(offset);

        List<Document> results = mongoTemplate.find(query, Document.class, MongoConstants.MONGO_BGG_C_NAME);
        return results;
    }

    /*
        db.game.find({
            "gid": << the game id to be entered >>
        })
    */ 

    public List<Document> getGameById(int gameId){
        Criteria criteria = Criteria.where("gid").is(gameId);
        Query query = Query.query(criteria);
        List<Document> results = mongoTemplate.find(query, Document.class, MongoConstants.MONGO_BGG_C_NAME);
        return results;
    }

    /*
        db.comment.insert({
            "user" : <<-- username from form -->>,
            "rating" : <<-- rating from form -->>,,
            "c_text" : <<-- comment from form -->>,,
            "gid" : <<-- gid from form -->>,
        })
    */ 

    public ObjectId insertComment(Document newReview){
        return mongoTemplate.insert(newReview, MongoConstants.MONGO_BGG_C_NAME).getObjectId(newReview); 
    }

}
