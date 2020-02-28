package com.vedantu.daos;

import com.vedantu.models.CartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service
public class CartDao extends AbstractMongoDao{

    @Autowired
    private  MongoConnection mongoConnection;

    @Override
    protected MongoOperations getMongoOperations() {
        return mongoConnection.getMongoOperations();
    }

    public Boolean addToCart(CartModel cartModel, Long addedBy) throws Exception {
        try{
            /* saves if _id is not there or else updates  it */
            saveEntity(cartModel, addedBy, "cartitems");
            return true;
        }catch(Exception e){
            throw new Exception(e);
        }
    }

}
