package com.vedantu.managers;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.vedantu.daos.CounterDao;
import com.vedantu.daos.ProductDao;
import com.vedantu.models.ProductModel;
import com.vedantu.requests.ProductRequest;
import com.vedantu.security.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ProductManager {

    private final CounterDao counterDao; // injection will be done in constructor
    private final ProductDao productDao; // injection will be done in constructors

    @Autowired
    public ProductManager(CounterDao counterDao, ProductDao productDao){
        this.counterDao = counterDao;
        this.productDao = productDao;
    }

    // addProduct (can only accessed to merchant)
    public boolean addProduct(ProductRequest productRequest) throws Exception {
        productRequest.verify();

        // converting ProductRequest to ProductModel to insert into db
        ProductModel productModel = new ProductModel(productRequest);
        if(productModel.getId() == null || productModel.getId() == 0L){
            productModel.setId(counterDao.nextSequence("products"));
        }

        /* get the current userId (here merchantId) from the SecurityContext
        * and set it to productModel.setMerchantId()
        * */
        Long currentUserId = getCurrentLoggedInUserId();

        // setting the merchantId
        productModel.setMerchantId(currentUserId);

        return productDao.addProduct(productModel, currentUserId);
    }

    // updateProduct by merchant or an admin
    public Boolean updateProduct(ProductRequest updateRequest) throws Exception {
        // check for productId
        if(updateRequest.getProductId() == null || updateRequest.getProductId() == 0L){
            throw new Exception("productId is mandatory to update a product");
        }

        // check whether productId is valid or not
        ProductModel product = productDao.getEntityById(updateRequest.getProductId(), ProductModel.class);
        if(product == null){
            throw new Exception("invalid productId");
        }
        /* check whether updater is either product owner or amdin */
        if(product.getMerchantId() != getCurrentLoggedInUserId() && isCurrentUserAdmin() == false){
            throw new Exception("Trying to unauthorized access");
        }

        /* updating fields of product based on the updateRequest */
        if(updateRequest.getProductName() != null && updateRequest.getProductName() != ""){
            product.setProductName(updateRequest.getProductName());
        }
        if(updateRequest.getProductQuantity() != null){
            product.setProductQuantity(updateRequest.getProductQuantity());
        }
        if(updateRequest.getProductPricePerUnit() != null && updateRequest.getProductPricePerUnit() != 0){
            product.setProductPricePerUnit(updateRequest.getProductPricePerUnit());
        }
        if(updateRequest.getProductDescription() != null && updateRequest.getProductDescription() != ""){
            product.setProductDescription(updateRequest.getProductDescription());
        }
        /* everything is set. And we can update it */
        return productDao.updateProduct(product, getCurrentLoggedInUserId());
    }

    /* delete productId - corresponding customer or admin can delete */
    public Boolean deleteProduct(Long productId) throws Exception {
        if(productId == null || productId == 0){
            throw  new Exception("productId is required");
        }

        /* check currentLoggedInUser is admin or not */
        Long currentLoggedInUser = getCurrentLoggedInUserId();

        if(isCurrentUserAdmin()){
            return productDao.deleteProduct(productId);
        }
        /* means currentUser is not an admin
        * So, we need to check whether entered productId is associated with this merchant or not
        *   */
        ProductModel productModel = productDao.getEntityById(productId, ProductModel.class, "products");
        if(productModel != null){
            if(productModel.getMerchantId() != currentLoggedInUser){
                throw new  Exception("unauthorized access to product");
            }
        }

        /* means, productId is not there or productId is current merchant's one */
        return productDao.deleteProduct(productId);
    }


    /* BEGIN: Methods related  to security context */
    public Long getCurrentLoggedInUserId() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth.getPrincipal() instanceof  UserDetails)){
            throw new Exception("currentLoggedInUserId not found in the SecurityContext");
        }

        CustomUser currentUser = (CustomUser)auth.getPrincipal();

        return currentUser.getUserId();
    }

    public ArrayList<GrantedAuthority> getCurrentUserAuthorities(){
        return new ArrayList<GrantedAuthority>(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }

    public Boolean isCurrentUserAdmin(){
        ArrayList<GrantedAuthority> list = getCurrentUserAuthorities();
        boolean admin = false;
        for(GrantedAuthority role : list){
            if(role.toString().equals("ROLE_ADMIN")){
                admin = true;
                break;
            }
        }
        return admin;
    }

    /* END: Methods related to security context */


}
