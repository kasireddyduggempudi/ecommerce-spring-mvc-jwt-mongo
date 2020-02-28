package com.vedantu.daos;

import com.vedantu.models.CounterModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class CounterDao {

    private MongoConnection mongoConnection;    // injection will be done in constructor

    @Autowired
    public CounterDao(MongoConnection mongoConnection){
        this.mongoConnection = mongoConnection;
    }



    public MongoOperations getMongoOperations(){
        return mongoConnection.getMongoOperations();
    }

    public Long nextSequence(String collectionName){

        Query query = new Query(Criteria.where("_id").is(collectionName));
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        options.upsert(true);   // will insert the collectionName and sequence if not in the counter collection

        Update update = new Update();
        update.inc("sequence", 1);  // incrementing one to previous

        CounterModel counter = this.getMongoOperations().findAndModify(query, update, options, CounterModel.class);

        return counter.getSequence();
    }

}
