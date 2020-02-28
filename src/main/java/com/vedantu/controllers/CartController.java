package com.vedantu.controllers;

import com.vedantu.daos.CartDao;
import com.vedantu.managers.CartManager;
import com.vedantu.models.CartItem;
import com.vedantu.requests.CartRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartManager cartManager;

    /* Only customer can add to his cart */
    @RequestMapping(value = "/addToCart", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public Boolean addToCart(@RequestBody CartRequest cartRequest) throws Exception {
        return cartManager.addToCart(cartRequest);
    }


    /* update cartItem  and only customer can do it*/
    @RequestMapping(value = "/updateCartItem", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public  Boolean updateCartItem(@RequestBody CartRequest cartRequest) throws Exception {
        return cartManager.updateCartItem(cartRequest);
    }

    /* delete cartItem and only customer can do it*/
    @RequestMapping(value = "/deleteCartItem/{productId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public Boolean deleteCartItem(@PathVariable("productId") Long productId) throws  Exception{
        return cartManager.deleteCartItem(productId);
    }

    /* getCartItems for a customer */
    @RequestMapping("/getCartItems/{customerId}")
    public ArrayList<CartItem> getCartItemsByCustomerId(@PathVariable("customerId") Long customerId) throws Exception{
        return cartManager.getCartItemsByCustomerId(customerId);
    }

}
