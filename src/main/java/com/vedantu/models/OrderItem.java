package com.vedantu.models;

public class OrderItem {
    private Long productId;
    private Long productQuantity;
    private Long productPricePerUnit;

    public OrderItem(){
        super();
    }

    public OrderItem(Long productId, Long productQuantity, Long productPricePerUnit){
        this.productId = productId;
        this.productQuantity = productQuantity;
        this.productPricePerUnit = productPricePerUnit;
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

    public Long getProductPricePerUnit() {
        return productPricePerUnit;
    }

    public void setProductPricePerUnit(Long productPricePerUnit) {
        this.productPricePerUnit = productPricePerUnit;
    }
}
