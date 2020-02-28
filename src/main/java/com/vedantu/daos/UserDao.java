package com.vedantu.daos;

import com.vedantu.models.AbstractMongoEntity;
import com.vedantu.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class UserDao extends AbstractMongoDao {

    private final MongoConnection mongoConnection; // injection will be done in constructor


    @Autowired // even we can remove this since only one constructor as this class is bean
    public UserDao(MongoConnection mongoConnection) {
        this.mongoConnection = mongoConnection;
    }


    public MongoOperations getMongoOperations(){
        // this will be used by the abstractmongodao class method to which this class is subclass
        return mongoConnection.getMongoOperations();
    }

    // create User
    public boolean createUser(UserModel user) throws Exception {
        try{
            saveEntity(user);
            return true;
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    /* getUserById */
    public UserModel getUserById(Long id) throws Exception {
        return getEntityById(id, UserModel.class, "users");
    }

    // getUserByPhoneNumber
    /* Because, phoneNumber is unique we are using */
    public UserModel getUserByPhoneNumber(String phoneNumber) throws Exception {
        Query query = new Query(Criteria.where("phoneNumber").is(phoneNumber));
        UserModel userModel = getEntity(query, UserModel.class, "users");
        return userModel;
    }

    // updateUser
    public Boolean updateUser(UserModel updateUserModel, Long updatedBy) throws Exception {
        try{
            saveEntity(updateUserModel, updatedBy);
            return true;
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    // getUserById



    //


}
