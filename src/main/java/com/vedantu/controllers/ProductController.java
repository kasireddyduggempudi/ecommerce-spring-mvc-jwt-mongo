package com.vedantu.controllers;


import com.vedantu.managers.ProductManager;
import com.vedantu.models.ProductModel;
import com.vedantu.requests.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductManager productManager;  // injection will be done in constructor

    // constructor dependency injection
    @Autowired
    public ProductController(ProductManager productManager){
        this.productManager = productManager;
    }

    /* Method */


    // only merchant can add , not even admin
    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    public Boolean addProduct(@RequestBody ProductRequest productRequest) throws Exception {
        return productManager.addProduct(productRequest);
    }


    @RequestMapping(value = "/updateProduct", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_MERCHANT', 'ROLE_ADMIN')")
    public Boolean updateProduct(@RequestBody ProductRequest updateRequest) throws  Exception{
        return productManager.updateProduct(updateRequest);
    }

    @RequestMapping(value = "/deleteProduct/{productId}", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ROLE_MERCHANT', 'ROLE_ADMIN')")
    public Boolean deleteProduct(@PathVariable Long productId) throws Exception {
        return productManager.deleteProduct(productId);
    }
}
