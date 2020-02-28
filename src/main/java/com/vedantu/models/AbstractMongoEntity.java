package com.vedantu.models;

public abstract class AbstractMongoEntity {

    private Long createdTime;
    private Long createdById;
    private Long lastUpdatedTime;
    private Long lastUpdatedById;

    public AbstractMongoEntity(){
        super();
    }

    public abstract  void setDefaultEntityProperties(Long createdById);

    public AbstractMongoEntity(Long createdTime, Long createdById, Long lastUpdatedTime, Long lastUpdatedById){
        this.createdTime = createdTime;
        this.createdById = createdById;
        this.lastUpdatedTime = lastUpdatedTime;
        this.lastUpdatedById = lastUpdatedById;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Long getLastUpdatedById() {
        return lastUpdatedById;
    }

    public void setLastUpdatedById(Long lastUpdatedById) {
        this.lastUpdatedById = lastUpdatedById;
    }
}
