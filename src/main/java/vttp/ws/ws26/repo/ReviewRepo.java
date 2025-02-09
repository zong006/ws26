package vttp.ws.ws26.repo;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepo {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    // public ObjectId createReviewRecord(){

    // }
}
