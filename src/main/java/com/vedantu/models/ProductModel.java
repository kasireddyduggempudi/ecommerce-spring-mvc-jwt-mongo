package com.vedantu.models;

import com.vedantu.requests.ProductRequest;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(collection = "products")
public class ProductModel extends AbstractMongoEntity{
    @Id
    private Long id;
    private Long merchantId;
    private String productName;
    private Long productQuantity;   // in units
    private Long productPricePerUnit;
    private String productDescription;

    // default constructor

    public ProductModel(){
        super();
    }

    // converting ProductRequest to ProductModel

    public ProductModel(ProductRequest productRequest){
        // product Id will be set in default or in managers
        this.productName = productRequest.getProductName();
        this.productQuantity = productRequest.getProductQuantity();
        this.productPricePerUnit = productRequest.getProductPricePerUnit();
        this.productDescription = productRequest.getProductDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

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
