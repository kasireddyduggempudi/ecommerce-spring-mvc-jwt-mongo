package com.vedantu.requests;

import java.util.List;

public class CartRequest extends AbstractFrontEndReq {
    private Long productId;
    private Long productQuantity;
    // productPrice is taken from the products collection


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
        List<String> errorsList = super.collectValidationErrors();

        // productId is mandatory
        if(this.productId == null){
            errorsList.add("productId is mandatory");
        }

        // productQuantity is mandatory
        if(this.productQuantity == null || this.productQuantity <= 0){
            errorsList.add("productQuantity is mandatory");
        }

        return errorsList;
    }
}
