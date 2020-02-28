package com.vedantu.requests;

import java.util.ArrayList;
import java.util.List;

public class OrderRequest extends AbstractFrontEndReq {
    /*
    This is request sent from user from user's Cart
    Order can for one CartItem or all All CartItems
     */

    private Long productId;
    private Long productQuantity;

    public OrderRequest(){
        super();
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    @Override
    public List<String> collectValidationErrors() {
        List<String> errors = new ArrayList<String>();

        if(this.productId == null || this.productId <= 0){
            errors.add("productId is mandatory to order a product.");
        }

        if(this.productQuantity == null || this.productQuantity <= 0){
            errors.add("productQuantity is mandatory to order a product.");
        }

        return  errors;
    }
}
