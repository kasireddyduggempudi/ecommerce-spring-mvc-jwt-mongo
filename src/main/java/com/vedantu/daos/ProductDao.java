package com.vedantu.daos;

import com.mongodb.Mongo;
import com.vedantu.models.ProductModel;
import com.vedantu.requests.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service
public class ProductDao extends  AbstractMongoDao {

    private final MongoConnection mongoConnection; // injection will be done in constructor

    @Autowired
    public ProductDao(MongoConnection mongoConnection){
        this.mongoConnection = mongoConnection;
    }

    @Override
    protected MongoOperations getMongoOperations() {
        return this.mongoConnection.getMongoOperations();
    }

    /* Methods */
    public boolean addProduct(ProductModel productModel, Long addedById) throws Exception {
        try{
            saveEntity(productModel, addedById);
            return true;
        }catch(Exception e){
            throw new Exception(e);
        }
    }


    public Boolean updateProduct(ProductModel newProduct, Long updatedBy) throws Exception {
        try{
            saveEntity(newProduct, updatedBy, "products");
            return  true;
        }catch (Exception e){
            throw  new Exception(e);
        }
    }

    public Boolean deleteProduct(Long productId) throws Exception {
        try {
            deleteEntityById(productId, ProductModel.class, "products");
            return true;
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    /* getProductById */
    public ProductModel getProductById(Long id) throws Exception {
        return getEntityById(id, ProductModel.class, "products");
    }
}
