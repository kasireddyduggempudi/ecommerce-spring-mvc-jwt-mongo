package com.vedantu.requests;

import com.vedantu.requests.AbstractFrontEndReq;

import java.util.List;

public class ProductRequest extends AbstractFrontEndReq {
    private Long productId; // useful in updating, because nothing is there uniquely here. or else we can write new class for update fields and can remove this from here
    private String productName;
    private Long productQuantity; // in units
    private Long productPricePerUnit;
    private String productDescription;  // optional

    public Long getProductId(){return productId;}

    public void setProductId(Long productId){this.productId = productId;}

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    @Override
    public List<String> collectValidationErrors(){
        List<String> errors = super.collectValidationErrors();
        /*if(this.productId == null || this.productId == 0L){
            errors.add("productId is mandatory field");
        }*/

        if(this.productName == null || this.productName.equals("")){
            errors.add("productName is mandatory field");
        }

        if(this.productQuantity == null || this.productQuantity < 0){
            errors.add("productQuantity is mandatory field");
        }

        if(this.productPricePerUnit == null || this.productPricePerUnit <= 0){
            errors.add("productPricePerUnit is mandatory field");
        }

        return errors;
    }

}
