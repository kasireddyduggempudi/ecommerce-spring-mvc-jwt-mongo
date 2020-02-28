package com.vedantu.models;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Document(collection = "cartitems")
public class CartModel extends AbstractMongoEntity {
    @Id
    private Long id;
    private Long customerId;    // which customers this cart is
    private List<CartItem> cartList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getCartList() {
        return cartList;
    }

    public void setCartList(List<CartItem> cartList) {
        this.cartList = cartList;
    }

    // setting default createdBy and createdTime
    public void setDefaultEntityProperties(Long createdBy){
        // we are extending AbstractMongoEntity
        // so we can access methods, but not properties since private

        // basically this is called before saving or updating
        // so we need to check if this object has already id
        // if id is there, just set the lastUpdatedBy and lastUpdatedTime

        if(this.getCreatedTime() == null){
            // means first time. this object saving
            this.setCreatedById(createdBy);
            this.setCreatedTime(System.currentTimeMillis());
        }
        // setting lastUpdatedTime and lastUpdatedBy
        this.setLastUpdatedTime(System.currentTimeMillis());
        this.setLastUpdatedById(createdBy);
    }
}
