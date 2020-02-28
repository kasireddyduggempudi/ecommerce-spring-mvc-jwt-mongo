//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package  com.vedantu.daos;


import com.mongodb.client.result.UpdateResult;
import com.vedantu.models.AbstractMongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;


public abstract class AbstractMongoDao {

    /*
    NOTE: Class<E> means E.class => eg.: user.class (package.className)
     */

    public static final String ORDER_DESC = "desc";
    public static final String ORDER_ASC = "asc";
    public static final String QUERY_AND_OPERATOR = "&&";
    public static final long NO_LIMIT = 0L;
    public static final long UNINITIALIZED = -1L;
    public static final int DEFAULT_FETCH_SIZE = 20;
    public static final int MAX_ALLOWED_FETCH_SIZE = 2000;
    /*private static final List<String> AUDIT_LIST = new ArrayList(Arrays.asList("BaseInstalment", "Batch", "Course", "BundleEnrolment", "Orders", "Enrollment", "Instalment", "Transaction", "CMDSQuestion", "CMDSTest", "ContentInfo", "GTTAttendeeDetails", "OTFSession", "Session", "Bundle", "OTMBundle", "SessionAttendee", "BundlePackage"));*/


    // this will be overridden in subclassess
    // which will give MongoOperation instance
    // that is using here
    protected abstract MongoOperations getMongoOperations();


    protected <E extends AbstractMongoEntity> void saveEntity(E entityObject, Long createdById) {
        entityObject.setDefaultEntityProperties(createdById); /* this sets createdTime, by, lastUPdatedTime, by */
        this.getMongoOperations().save(entityObject); // no need to give collection name
        // because annotated with @Document(collection = name) in respective model (E here) class
    }

    protected <E extends AbstractMongoEntity> void saveEntity(E entityObject) {
        entityObject.setDefaultEntityProperties(null);
        this.getMongoOperations().save(entityObject);
    }

    protected <E extends AbstractMongoEntity> void saveEntity(E entityObject, Long createdById, String collectionName) {
        entityObject.setDefaultEntityProperties(createdById); /* this sets createdTime, by, lastUpdatedTime, by */
        this.getMongoOperations().save(entityObject, collectionName);
    }

    protected  <E extends  AbstractMongoEntity> void saveEntities(List<E> entities, Long createdBy, String collectionName){
        // this  set createdTime, by, lastUpdatedTime, by for each object
        for(E entityObject:entities){
            entityObject.setDefaultEntityProperties(createdBy);
        }
        this.getMongoOperations().insert(entities, collectionName);
    }

    public <E extends AbstractMongoEntity> void upsertEntity(Query query, Update update, Class<E> entityClass) {
        this.getMongoOperations().upsert(query, update, entityClass);
    }


    public <E extends AbstractMongoEntity> void upsertEntity(Query query, Update update, String collectionName) {
        this.getMongoOperations().upsert(query, update, collectionName);
    }

    public <E extends AbstractMongoEntity> void upsertEnity(Query query, Update update, Class<E> entityClass, String collectionName) {
        this.getMongoOperations().upsert(query, update, entityClass, collectionName);
    }


    public <E extends AbstractMongoEntity> E updateEntity(Query query, Update update, FindAndModifyOptions options, Class<E> entityClass) {
        E modifiedDoc = this.getMongoOperations().findAndModify(query, update, options, entityClass);
        return modifiedDoc;
    }

    public <E extends AbstractMongoEntity> E updateEntity(Query query, Update update, FindAndModifyOptions options, Class<E> entityClass, String collectionName) {
        E modifiedDoc = this.getMongoOperations().findAndModify(query, update, options, entityClass, collectionName);
        return modifiedDoc;
    }

    public <T extends AbstractMongoEntity> T getEntityById(Long id, Class<T> entityClass) throws Exception {
        Query query = new Query(Criteria.where("_id").is(id));

        try {
            T doc = this.getMongoOperations().findOne(query, entityClass);
            return doc;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T extends AbstractMongoEntity> T getEntityById(Long id, Class<T> entityClass, String collectionName) throws Exception {
        Query query = new Query(Criteria.where("_id").is(id));

        try {
            T doc = this.getMongoOperations().findOne(query, entityClass, collectionName);
            return doc;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T extends AbstractMongoEntity> T getEntityById(Long id, Class<T> entityClass, String collectionName, List<String> includeFieldList) throws Exception {
        Query query = new Query(Criteria.where("_id").is(id));
        if (includeFieldList.size() > 0) {
            includeFieldList.forEach((field) -> {
                query.fields().include(field);
            });
        }
        try {
            T newDoc = this.getMongoOperations().findOne(query, entityClass, collectionName);
            return newDoc;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public <T extends AbstractMongoEntity> T getEntity(Query query, Class<T> entityClass) throws Exception {
        try {
            T resultDoc = this.getMongoOperations().findOne(query, entityClass);
            return resultDoc;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T extends AbstractMongoEntity> T getEntity(Query query, Class<T> entityClass, String collectionName) throws Exception {
        try {
            T resultDoc = this.getMongoOperations().findOne(query, entityClass, collectionName);
            return resultDoc;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T extends AbstractMongoEntity> List<T> getEntities(Query query, Class<T> entityClass) throws Exception {
        try {
            List<T> results = this.getMongoOperations().find(query, entityClass);
            return results;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T extends AbstractMongoEntity> List<T> getEntities(Query query, Class<T> entityClass, String collectionName) throws Exception {
        try {
            List<T> results = this.getMongoOperations().find(query, entityClass, collectionName);
            return results;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public <T extends AbstractMongoEntity> T deleteEntityById(Long id, Class<T> entityClass) {
        Query query = new Query(Criteria.where("_id").is(id));
        T deletedDoc = this.getMongoOperations().findAndRemove(query, entityClass);
        return deletedDoc;
    }

    public <T extends AbstractMongoEntity> T deleteEntityById(Long id, Class<T> entityClass, String collectionName) {
        Query query = new Query(Criteria.where("_id").is(id));
        T deletedDoc = this.getMongoOperations().findAndRemove(query, entityClass, collectionName);
        return deletedDoc;
    }

    public <T extends AbstractMongoEntity> List<T> deleteEntities(Query query, Class<T> entityClass) {
        List<T> deletedDocs = this.getMongoOperations().findAllAndRemove(query, entityClass);
        return deletedDocs;
    }

    public <T extends AbstractMongoEntity> List<T> deleteEntities(Query query, Class<T> entityClass, String collectionName) {
        List<T> deletedDocs = this.getMongoOperations().findAllAndRemove(query, entityClass, collectionName);
        return deletedDocs;
    }

}