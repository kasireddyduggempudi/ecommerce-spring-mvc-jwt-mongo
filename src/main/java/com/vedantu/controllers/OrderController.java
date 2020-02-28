package com.vedantu.controllers;

import com.vedantu.managers.OrderManager;
import com.vedantu.models.OrderModel;
import com.vedantu.requests.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderManager orderManager;

    /* only done by CUSTOMER */
    @RequestMapping(value = "/addOrder", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public Boolean addOrder(@RequestBody OrderRequest orderRequest) throws Exception {
        return orderManager.addOrder(orderRequest);
    }

    /* order all items in  the cart
       only done by CUSTOMER
       we dont need any data from the user. Based on his currentLoggedInUser we order.
     */
    @RequestMapping(value = "/orderAllCartItems", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public Boolean orderAllCartItems() throws Exception {
        return orderManager.orderAllCartItems();
    }

    /* cancelOrder  using orderId*/
    @RequestMapping(value = "/cancelOrder/{orderId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public Boolean cancelOrder(@PathVariable("orderId") String orderId) throws Exception {
        return orderManager.cancelOrder(orderId);
    }

    /* getOrders => current logged in user order details*/
    @RequestMapping(value="/getOrders")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public List<OrderModel> getOrder() throws Exception {
        return orderManager.getOrders();
    }
}
