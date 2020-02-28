package com.vedantu.models;

import com.vedantu.requests.CartRequest;

public class CartItem {
    // this will store as cart item  in the cart list of customer
    private Long productId;
    private Long productQuantity;

    public CartItem(){
        super();
    }

    public CartItem(CartRequest cartRequest){
        this.productId = cartRequest.getProductId();
        this.productQuantity = cartRequest.getProductQuantity();
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
}
