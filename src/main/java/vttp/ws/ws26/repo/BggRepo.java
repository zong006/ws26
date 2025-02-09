package vttp.ws.ws26.repo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

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
        List<Document> results = mongoTemplate.find(query, Document.class, MongoConstants.MONGO_BGG_C_NAME_GAMES);

        return results;
    }

    /*
        db.game.find({}).sort({"ranking":1}).limit("number to limit").skip("number to offset")
    */ 

    public List<Document> getGamesByRank(int limit, int offset){

        Criteria criteria = Criteria.where(null);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.ASC, MongoConstants.MONGO_BGG_RANKING)).limit(limit).skip(offset);

        List<Document> results = mongoTemplate.find(query, Document.class, MongoConstants.MONGO_BGG_C_NAME_GAMES);
        return results;
    }

    /*
        db.game.find({
            "gid": << the game id to be entered >>
        })
    */ 

    public List<Document> getGameById(Integer gameId){
        Criteria criteria = Criteria.where("gid").is(gameId);
        Query query = Query.query(criteria);
        List<Document> results = mongoTemplate.find(query, Document.class, MongoConstants.MONGO_BGG_C_NAME_GAMES);
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
        Document d = mongoTemplate.insert(newReview, MongoConstants.MONGO_BGG_C_NAME_REVIEWS); 
        ObjectId id = d.getObjectId("_id");
        return id;
    }


    public List<Document> getReviewById(String reviewId){
        Criteria criteria = Criteria.where("review_id").exists(true);
        Query query = Query.query(criteria);
        List<Document> results = mongoTemplate.find(query, Document.class, MongoConstants.MONGO_BGG_C_NAME_REVIEWS);
        return results;
    }

    /*
        db.reviews.updateOne(
            {"review_id":<<-- review id from path variable -->>},
            {
                $push: {
                    "edits": { 
                        comment: <<-- comment from request body-->>,
                        rating: <<-- rating from request body -->>,
                        timestamp: new ISODate()  
                    }
                }
            }    
        )
    */ 

    public long updateReview(String reviewId, Document updates){
        Criteria criteria = Criteria.where("review_id").is(reviewId);
        Query query = Query.query(criteria);

        Update update = new Update().push("edits", updates);
        
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, Document.class, MongoConstants.MONGO_BGG_C_NAME_REVIEWS);
        return updateResult.getModifiedCount();
    }

    /*
        -- check if comment contains edits first
        db.reviews.find({
            "review_id": <<-- review id from path variabe -->>,
            "edits" : {
                $exists : 1
            }
        })

        -- if no results returned, do a normal query with review_id
        db.reviews.find(
            {
            "review_id": <<-- review id from path variabe -->>
            },
            {    
                "user":1,
                "rating":1,
                "comment":1,
                "posted":1,
                "gid":1,
                "name":1,
                edited : "true",
                timestamp : new ISODate()
                
            }
        )
        
        -- else, there are results, and get the results with the latest comment

        db.getCollection("reviews").aggregate([
            {
                $match:{"review_id" : <<-- review id from path variabe -->>}
            },
            {
                $unwind : "$edits"
            },
            {
                $sort : {"edits.timestamp":-1}
            },
            {
                $limit:1
            },
            {
                $project:{
                    "user":1,
                    "rating":1,
                    comment : "$edits.comment",
                    posted : "$edits.timestamp",
                    "gid":1,
                    "name":1,
                    timestamp: new ISODate()
                }
            }
        ])
    */ 

    public Optional<Document> showLatestReview(String reviewId){

        Criteria matchCriteria = Criteria.where("review_id").is(reviewId);
        Criteria editsExist = Criteria.where("edits").exists(true);
        List<Criteria> criterion = Arrays.asList(matchCriteria, editsExist);

        Criteria criteria = new Criteria().andOperator(criterion);
        Query query = Query.query(criteria);
        List<Document> firstResult = mongoTemplate.find(query, Document.class, MongoConstants.MONGO_BGG_C_NAME_REVIEWS);
        
        
        Optional<Document> result;
        if (firstResult.size()>0){

            MatchOperation matchReviewId = Aggregation.match(matchCriteria);
            AggregationOperation unwindEdits = Aggregation.unwind("edits");
            SortOperation sortByTimeStamp = Aggregation.sort(Sort.by(Direction.DESC, "edits.timestamp"));
            LimitOperation selectLatest = Aggregation.limit(1);
            ProjectionOperation selectFields = Aggregation.project("user", "rating")
                                                            .and("edits.comment").as("comment")
                                                            .and("edits.timestamp").as("posted")
                                                            .andInclude("gid")
                                                            .andInclude("name")
                                                            ;
            AddFieldsOperation addFields = Aggregation.addFields().addField("edited").withValue(true)
                                                                    .build();
            Aggregation pipeline = Aggregation.newAggregation(matchReviewId,
                                                                        unwindEdits,
                                                                        sortByTimeStamp,
                                                                        selectLatest,
                                                                        selectFields,
                                                                        addFields);
            result = Optional.ofNullable(mongoTemplate.aggregate(pipeline, MongoConstants.MONGO_BGG_C_NAME_REVIEWS, Document.class)
                                                                            .getMappedResults()
                                                                            .getFirst());
        }
        else {
            
            Query query2 = Query.query(matchCriteria);
            result = Optional.ofNullable(mongoTemplate.find(query2, Document.class, MongoConstants.MONGO_BGG_C_NAME_REVIEWS)
                                    .getFirst());
            
        }
        
        return result;
    }

    public Optional<Document> getReviewHistory(String reviewId){
        Criteria criteria = Criteria.where("review_id").is(reviewId);
        Query query = Query.query(criteria);
        Optional<Document> opt =  Optional.ofNullable(mongoTemplate.find(query, Document.class, MongoConstants.MONGO_BGG_C_NAME_REVIEWS).getFirst());
        return opt;
    }

    public Optional<List<Document>> getAllReviews(){
        Criteria criteria = Criteria.where(null);
        Query query = Query.query(criteria);
        Optional<List<Document>> opt = Optional.ofNullable(mongoTemplate.find(query, Document.class, MongoConstants.MONGO_BGG_C_NAME_REVIEWS));
        return opt;
    }

    /*
        db.games.aggregate([
            {
                $match: {
                    "gid":32
                }
            },
            {
                $lookup: {
                    from: "comment",
                    localField: "gid",
                    foreignField: "gid",
                    as: "reviews"
                }
            }
        ])
    */ 

    public List<Document> getGameWithComments(int gameId){
        MatchOperation matchGameId = Aggregation.match(Criteria.where("gid").is(gameId));
        LookupOperation lookupOperation = Aggregation.lookup(MongoConstants.MONGO_BGG_C_NAME_COMMENT,
                                                                "gid",
                                                                 "gid",
                                                                  "reviews");
        Aggregation pipeline = Aggregation.newAggregation(matchGameId, lookupOperation);

        List<Document> results = mongoTemplate.aggregate(pipeline, MongoConstants.MONGO_BGG_C_NAME_GAMES, Document.class)
                                                                    .getMappedResults();

        return results;                                                           
    }

    /*
        db.games.aggregate([
            {
                $lookup: {
                    from: "comment",
                    localField: "gid",
                    foreignField: "gid",
                    pipeline: [
                        {
                            $sort:{
                                "rating":-1  <<----- change to 1 for lowest rating
                            }
                        },
                        {
                            $limit :1
                        }
                    ],
                    as: "reviews"
                }
            },
            {
                $unwind : "$reviews"
            },
            {
                $project :{
                    "_id":"$gid",
                    "name":1,
                    rating : "$reviews.rating",
                    user : "$reviews.user",
                    comment : "$reviews.c_text",
                    review_id : "$reviews._id"
                }
            }
        ])
    */ 

    public List<Document> getGamesWithHighestOrLowestRating(boolean fromHighest){

        SortOperation sortOperation;
        if (fromHighest){
            sortOperation = Aggregation.sort(Direction.DESC,"rating");
        }
        else{
            sortOperation = Aggregation.sort(Direction.ASC, "rating");
        }
        LimitOperation limitOne = Aggregation.limit(1);

        LookupOperation lookupOperation = LookupOperation.newLookup().from(MongoConstants.MONGO_BGG_C_NAME_COMMENT)
                                                                    .localField("gid")
                                                                    .foreignField("gid")
                                                                    .pipeline(sortOperation, limitOne)
                                                                    .as("reviews");
        UnwindOperation unwindReviews = Aggregation.unwind("reviews");
        ProjectionOperation projectionOperation = Aggregation.project("name")
                                                            .and("reviews.rating").as("rating")
                                                            .and("reviews.user").as("user")
                                                            .and("reviews.c_text").as("comment")
                                                            .and("reviews._id").as("reviews_id")
                                                            .and("gid").as("_id");

        Aggregation pipeline = Aggregation.newAggregation(lookupOperation, unwindReviews, projectionOperation);
        List<Document> results = mongoTemplate.aggregate(pipeline, MongoConstants.MONGO_BGG_C_NAME_GAMES, Document.class).getMappedResults();                
        return results;
    }

}
