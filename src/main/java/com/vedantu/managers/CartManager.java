package com.vedantu.managers;

import com.vedantu.daos.CartDao;
import com.vedantu.daos.CounterDao;
import com.vedantu.models.CartItem;
import com.vedantu.models.CartModel;
import com.vedantu.models.ProductModel;
import com.vedantu.requests.CartRequest;
import com.vedantu.security.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CartManager {

    @Autowired
    private CartDao cartDao;

    @Autowired
    private CounterDao counterDao;

    /* only customer can add */
    public Boolean addToCart(CartRequest cartRequest) throws Exception {
        cartRequest.verify();

        /* check if productId is valid or not */
        ProductModel product = cartDao.getEntityById(cartRequest.getProductId(), ProductModel.class, "products");
        if(product == null){
            throw new Exception("Invalid product id");
        }

        /* from user side, no problem to now. Now make request into model of cart and save in db */

        /* converting cartRequest to cartModel starts here. Below is cartItem which is part of CartModel*/
        CartItem cartItem = new CartItem(cartRequest);
        Long currentLoggedInUserId = getCurrentLoggedInUserId();

        /* fetch the currentLoggedInUser's cart */
        Query query = new Query(Criteria.where("customerId").is(currentLoggedInUserId));
        CartModel cartModel = cartDao.getEntity(query, CartModel.class, "cartitems");

        if(cartModel == null){
            // means no entry found for this customer in the cart
            cartModel = new CartModel();
            cartModel.setCustomerId(currentLoggedInUserId);
            // creating list for this user
            ArrayList<CartItem> list = new ArrayList<CartItem>();
            list.add(cartItem);
            cartModel.setCartList(list);
            if(cartModel.getId() == null){
                cartModel.setId(counterDao.nextSequence("cartitems"));
            }
        }else{
            /*Already entry is there for user.
            Just add the new cartItem to list and save
             */
            ArrayList<CartItem> list = (ArrayList<CartItem>) cartModel.getCartList();
            for(CartItem item:list){
                if(item.getProductId() == cartRequest.getProductId()){
                    throw new Exception("Product already in cart. Please update it for any changes.");
                }
            }
            list.add(cartItem);
            cartModel.setCartList(list);
        }
        /* Now, we got CartModel object. save in db */
        return cartDao.addToCart(cartModel, currentLoggedInUserId);
    }

    /* to update cartItem. Only customer can update */
    public Boolean updateCartItem(CartRequest cartRequest) throws Exception {
        cartRequest.verify();

        /* everything is find */
        /* check if productId is in customer's cart or not */
        Long currentLoggedInUserId = getCurrentLoggedInUserId();

        Query query = new Query(Criteria.where("customerId").is(currentLoggedInUserId));
        CartModel currentUserCart = cartDao.getEntity(query, CartModel.class, "cartitems");
        if(currentUserCart == null){
            throw new Exception("Your cart is empty to update. Please add items to your cart to update...");
        }
        /* check whether entered productId is in currentUser's cart */
        ArrayList<CartItem> list = (ArrayList<CartItem>) currentUserCart.getCartList();
        boolean isItemInCart = false;
        for(CartItem item:list){
            if(item.getProductId() == cartRequest.getProductId()){
                isItemInCart = true;
                /* at the same time, set the productQuantity here only to cartItem */
                item.setProductQuantity(cartRequest.getProductQuantity());
                break;
            }
        }

        if(!isItemInCart){
            throw new Exception("Entered productId is not in your cart. Please add it to update");
        }

//        /* Upto now, everything is there
//           Now, take the productQuantity from the request, and set it to that respected cartItem
//         */
//        Long productQuantity = cartRequest.getProductQuantity();
//        if(productQuantity == 0){
//            *//* means, delete the cartItem from the currentUserCart's list *//*
//            return this.deleteCartItem(cartRequest.getProductId());
//        }
//
//        *//* find the respected cartItem and update it and save the cartItem in db *//*
//        for(CartItem item:list){
//            if(item.getProductId() == cartRequest.getProductId()){
//                *//* here, update done *//*
//                item.setProductQuantity(cartRequest.getProductQuantity());
//            }
//        }
        /* Now, we have update list of currentUser. set the updated list to currentUserCart */
        currentUserCart.setCartList(list);
        // this will update the cart as _id is already there
        return cartDao.addToCart(currentUserCart, currentLoggedInUserId);
    }

    /* deleteCartItem */
    public Boolean deleteCartItem(Long productId) throws  Exception{
        /* productId is mandatory and it is enough */
        if(productId == null){
            throw new Exception("productId is required to delete an item from the cart.");
        }

        /* check if currentLoggedInUser has entered productId in his cart */
        Long currentLoggedInUser = getCurrentLoggedInUserId();
        Query query = new Query(Criteria.where("customerId").is(currentLoggedInUser));
        CartModel currentUserCart = cartDao.getEntity(query, CartModel.class, "cartitems");
        if(currentUserCart == null){
            throw new Exception("Cart is empty to delete any item.");
        }
        /* check currentUserCart has the productId or not in the cartList */
        ArrayList<CartItem> list = (ArrayList<CartItem>) currentUserCart.getCartList();
        boolean isItemInCart = false;
        for(CartItem item : list){
            if(item.getProductId() == productId){
                isItemInCart = true;
                /* at the same time delete from the list; */
                list.remove(item);
                break;
            }
        }

        if(!isItemInCart){
            throw  new Exception("Entered productId is not in your cart. Please add it to delete it.");
        }

        /* Now, we have updated list. set it to currentUserCart */
        currentUserCart.setCartList(list);

        /* Now save it to db */
        return cartDao.addToCart(currentUserCart, currentLoggedInUser);

    }

    /* getCartItemsByCustomerId to display the cartItems of a particular customer */
    public ArrayList<CartItem> getCartItemsByCustomerId(Long customerId) throws Exception {
        Query query = new Query(Criteria.where("customerId").is(customerId));
        CartModel cart = cartDao.getEntity(query, CartModel.class,"cartitems");
        if(cart == null){
            return null;
        }
        return new ArrayList<CartItem>(cart.getCartList());
    }


    /* BEGIN: Methods related  to security context */
    public Long getCurrentLoggedInUserId() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth.getPrincipal() instanceof UserDetails)){
            throw new Exception("currentLoggedInUserId not found in the SecurityContext");
        }


        CustomUser currentUser = (CustomUser)auth.getPrincipal();
        return currentUser.getUserId();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<GrantedAuthority> getCurrentUserAuthorities(){
        return new ArrayList<GrantedAuthority>(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }

    public Boolean isCurrentUserAdmin() {
        boolean admin = false;
        for (GrantedAuthority role : getCurrentUserAuthorities()) {
            System.out.println(role);
            if (role.toString().equals("ROLE_ADMIN")) {
                admin = true;
                break;
            }
        }
        return admin;
    }
    /* END: Methods related to security context */
}
