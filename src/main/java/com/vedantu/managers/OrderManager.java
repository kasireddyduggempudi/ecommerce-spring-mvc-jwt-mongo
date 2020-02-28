package com.vedantu.managers;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.vedantu.daos.CartDao;
import com.vedantu.daos.OrderDao;
import com.vedantu.daos.ProductDao;
import com.vedantu.daos.UserDao;
import com.vedantu.enums.OrderStatus;
import com.vedantu.models.*;
import com.vedantu.requests.OrderRequest;
import com.vedantu.security.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
public class OrderManager {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CartDao cartDao;


    /*
    addOrder()
    to order a product, it must be in the cart
     */
    public Boolean addOrder(@NotNull OrderRequest orderRequest) throws Exception {
        orderRequest.verify();

        /* get the currentLoggedInUserId */
        Long currentLoggedInUserId = getCurrentLoggedInUserId();
        /* get the currentUserCart based on userId */
        Query cartQuery = new Query(Criteria.where("customerId").is(currentLoggedInUserId));
        CartModel currentUserCart = cartDao.getEntity(cartQuery, CartModel.class, "cartitems" );
        if(currentUserCart == null || currentUserCart.getCartList().size() == 0){
            throw new Exception("Your cart is empty. Please add items to your cart.");
        }

        /* checking whether the current item is in the cart or not */
        /* productId and productQuantity must be same. if wants to change productQuantity, user
         - needs to update the cartItem and needs to order
         */
        boolean isItemPresent = false;
        for(CartItem cartItem:currentUserCart.getCartList()){
            if(cartItem.getProductId() == orderRequest.getProductId() && cartItem.getProductQuantity() == orderRequest.getProductQuantity()){
                isItemPresent = true;
                break;
            }
        }

        if(!isItemPresent){
            throw new Exception("Entered product is not in cart or productQuantity differs. Please ensure they are in cart");
        }

        /* so, entered product is in cart and quantity aso matches */
        /* fetch the currentUser details to know his amount in his wallet */
        UserModel userModel = userDao.getUserById(currentLoggedInUserId);
        long userAmount = userModel.getUserAmount();
        /* fetch the product Details from the products  and check for price and quantity*/
        ProductModel productModel = productDao.getProductById(orderRequest.getProductId());
        /* checking if quantity is more than what merchant has */
        if(orderRequest.getProductQuantity() > productModel.getProductQuantity()){
            throw new Exception("quantity is more than stock");
        }
        /* finding totalCost of this order. quantity * pricePerUnit */
        long totalCost = orderRequest.getProductQuantity() * productModel.getProductPricePerUnit();
        if(totalCost > userAmount){
            throw new Exception("In sufficient amount in your wallet. Please recharge with the help of admin");
        }

        /* now, cost ok, quantity ok */
        /* Now, we need to add this order to orders collection, update the user amount,  update the products collection, delete this item from the userCart. This should be done in one transaction */

        /* creating OrderModel object */
        OrderModel order = new OrderModel();
        OrderItem orderItem = new OrderItem(orderRequest.getProductId(), orderRequest.getProductQuantity(), productModel.getProductPricePerUnit());
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        orderItemList.add(orderItem);
        /* setting values to order */
        order.setCustomerId(currentLoggedInUserId);
        order.setOrderItemsList(orderItemList);
        order.setTotalAmount(totalCost);
        order.setOrderStatus(OrderStatus.PENDING);

        /* updating the userAmount */
        userModel.setUserAmount(userModel.getUserAmount() - totalCost);

        /* updating the product in whcich current user order */
        productModel.setProductQuantity(productModel.getProductQuantity() - orderRequest.getProductQuantity());

        /* deleting the current item from the cartList of currentUser */
        /* it can be even done in the above where we search if product is in cart */
        for(CartItem cartItem:currentUserCart.getCartList()){
            if(cartItem.getProductId() == orderRequest.getProductId()){
                currentUserCart.getCartList().remove(cartItem);
                /* currentUserCart is now not having this item. update this to cartitems collection */
                break;
            }
        }
        /* db operations */
        orderDao.addOrder(order, currentLoggedInUserId); /* saving order to orders collection */
        userDao.updateUser(userModel, currentLoggedInUserId); /* updating users amount */
        productDao.updateProduct(productModel, currentLoggedInUserId); /* updating products quantity */
        cartDao.addToCart(currentUserCart, currentLoggedInUserId);  /* updating cart of current user */

        return true;
    }

    /* orderAllCartItems */
    /* only user should access it */
    public Boolean orderAllCartItems() throws Exception {
        Long currentLoggedInUserId = getCurrentLoggedInUserId();

        /* fetch the currentUserCart */
        Query cartQuery = new Query(Criteria.where("customerId").is(currentLoggedInUserId));
        CartModel currentUserCart = orderDao.getEntity(cartQuery, CartModel.class, "cartitems");
        /* if it is empty, throw error */
        if(currentUserCart == null || currentUserCart.getCartList().size() == 0){
            throw new Exception("User cart is empty. Please add items to cart before ordering");
        }

        /* get the productId, productPricePerUnit from the products collections for each productId in the cartList */

        /* first fetch all the productIds and create hashMap with productId as key as and productQuantity as value */
        Map<Long, Long> cartQuantitiesMap = new HashMap<Long, Long>();
        for(CartItem cartItem:currentUserCart.getCartList()){
            cartQuantitiesMap.put(cartItem.getProductId(), cartItem.getProductQuantity());
        }
        /* now, cartQuantitiesMap contains, {productId :productQuantity, productId:productQuantity,...} */

        /* now fetch products corresponding to the cart  products */
        Query productPriceQuery = new Query(Criteria.where("_id").in(cartQuantitiesMap.keySet()));
        ArrayList<ProductModel> productsList = (ArrayList<ProductModel>) orderDao.getEntities(productPriceQuery, ProductModel.class, "products");

        /* check for orderQuantity <= stock for each order and find totalCost */
        long totalCost = 0;
        boolean quantityError = false;
        for(ProductModel productModel:productsList){
            if(productModel.getProductQuantity() < cartQuantitiesMap.get(productModel.getId())){
                quantityError = true;
                break;
            }
            totalCost+=(productModel.getProductPricePerUnit() * cartQuantitiesMap.get(productModel.getId()));
        }

        if(quantityError){
            throw new Exception("Invalid product quantity");
        }

        /* now, here we have totalCost and nothing wrong with quantity */

        /* before creating OrderModel object, check if currentLoggedInUser has amount >= totalCost */
        UserModel user = userDao.getUserById(currentLoggedInUserId);
        long userAmount = user.getUserAmount();
        if(userAmount < totalCost){
            throw new Exception("Insufficient amount in user account");
        }

        /* Now, user has sufficient balance and items in cart and valid productQuantities. */
        /* Now create OrderModel object. For that need to create List<OrderItem> and set it orderItemList */
        List<OrderItem> orderItemsList = new ArrayList<OrderItem>();
        /* create the OrderItem using productsList => which is currentCart products list */
        for(ProductModel productModel:productsList){
            OrderItem orderItem = new OrderItem(productModel.getId(), cartQuantitiesMap.get(productModel.getId()), productModel.getProductPricePerUnit());
            orderItemsList.add(orderItem);
        }
        /* creating OrderModel object */
        OrderModel orderModel = new OrderModel();
        orderModel.setCustomerId(currentLoggedInUserId);
        orderModel.setOrderItemsList(orderItemsList);
        orderModel.setTotalAmount(totalCost);
        orderModel.setOrderStatus(OrderStatus.PENDING);

        /* before saving this orderModel object in orders collection, we need to update, userAmount, currentUserCart, productQuantity in products */

        /* updating userAmount */
        user.setUserAmount(user.getUserAmount() - totalCost);

        /* updating currentUserCart */
        currentUserCart.setCartList(new ArrayList<CartItem>()); // setting it to empty

        /* updating products */
        List<ProductModel> updateProducts = new ArrayList<ProductModel>();
        for(ProductModel productModel:productsList){
            productModel.setProductQuantity(productModel.getProductQuantity() - cartQuantitiesMap.get(productModel.getId()));
            updateProducts.add(productModel);
        }


        // save orders, update userAmount, update user cart, update products in db

        /* saving orders */
        orderDao.addOrder(orderModel, currentLoggedInUserId);

        /* update userAmount */
        userDao.updateUser(user, currentLoggedInUserId);

        /* update products quantity */
        for(ProductModel updateProduct: updateProducts){
            productDao.updateProduct(updateProduct, currentLoggedInUserId);
        }

        /* update cart */
        cartDao.addToCart(currentUserCart, currentLoggedInUserId);
        return true;
    }

    /* cancelOrder using orderId */
    public Boolean cancelOrder(String orderId) throws Exception {
        /* fetch the orderDetails using orderId */
        Query orderQuery = new Query(Criteria.where("_id").is(orderId));
        OrderModel orderDetails = orderDao.getEntity(orderQuery, OrderModel.class, "orders");
        if(orderDetails == null){
            throw new Exception("Invalid orderId");
        }

        /* check for current status of order. cancel any order only if its status is PENDING */
        if(!orderDetails.getOrderStatus().equals(OrderStatus.PENDING)){
            throw new Exception("Your order is not in pending. Sorry, you can't cancel at this stage.");
        }

        /* check that orderId is associated with the currentLoggedInUserId */
        long currentLoggedInUserId = getCurrentLoggedInUserId();
        if(orderDetails.getCustomerId() != currentLoggedInUserId){
            throw new Exception("Unauthorized access to this order for this current");
        }

        /* creating HashMap for productId, productQuantity for updating products */
        Map<Long, Long> orderItemsMap = new HashMap<Long, Long>();
        for(OrderItem orderItem:orderDetails.getOrderItemsList()){
            orderItemsMap.put(orderItem.getProductId(), orderItem.getProductQuantity());
        }

        /* fetch the products info which are in this order */
        Query productQuery = new Query(Criteria.where("_id").in(orderItemsMap.keySet()));
        List<ProductModel> productsList = orderDao.getEntities(productQuery, ProductModel.class, "products");

        /* fetch the current user info */
        UserModel currentUser = userDao.getUserById(currentLoggedInUserId);

        /* now, we have do the following operations
            1) update the orderStatus and save it to db
            2) update the userAmount and save it to db
            3) update the productQuantities of each product and save it to db
         */

        /* update the orderDetails status and save it to db */
        orderDetails.setOrderStatus(OrderStatus.CANCELLED);
        orderDao.addOrder(orderDetails, currentLoggedInUserId);   // as  _id is set, it will update. save method

        /* updating current user amount */
        currentUser.setUserAmount(currentUser.getUserAmount() + orderDetails.getTotalAmount());
        userDao.updateUser(currentUser, currentLoggedInUserId);

        /* updating the products  quantities which are in this order items list*/
        for(ProductModel productModel:productsList){
            productModel.setProductQuantity(productModel.getProductQuantity() + orderItemsMap.get(productModel.getId()));
            productDao.updateProduct(productModel, currentLoggedInUserId);
        }
        return true;
    }

    /* getOrders => current logged in user orders details */
    public List<OrderModel> getOrders() throws Exception {
        long currentLoggedInUserId = getCurrentLoggedInUserId();

        /* fetch current logged in user orders */
        Query query = new Query(Criteria.where("customerId").is(currentLoggedInUserId));
        return orderDao.getEntities(query, OrderModel.class, "orders");
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

    public Boolean isCurrentUserAdmin(){
        boolean admin = false;
        for(GrantedAuthority role : getCurrentUserAuthorities()){
            System.out.println(role);
            if(role.toString().equals("ROLE_ADMIN")){
                admin = true;
                break;
            }
        }
        return admin;
    }


    /* END: Methods related to security context */
}
