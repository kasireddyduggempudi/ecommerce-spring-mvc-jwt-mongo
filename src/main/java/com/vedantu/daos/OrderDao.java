package com.vedantu.daos;

import com.vedantu.models.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service
public class OrderDao extends AbstractMongoDao{

    @Autowired
    private MongoConnection mongoConnection;

    @Override
    protected MongoOperations getMongoOperations() {
        return mongoConnection.getMongoOperations();
    }

    public Boolean addOrder(OrderModel orderModel, Long createdBy) throws Exception {
        try{
            saveEntity(orderModel, createdBy, "orders");
            return true;
        }catch(Exception e){
            throw new Exception(e);
        }
    }
}
