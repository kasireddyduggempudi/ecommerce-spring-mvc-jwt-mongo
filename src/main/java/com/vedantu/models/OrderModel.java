package com.vedantu.models;

import com.vedantu.enums.OrderStatus;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Document(collection = "orders")
public class OrderModel extends AbstractMongoEntity{

    @Id
    private String id;  // here mongo will generate auto id. Not depending on counter this time.
    private Long customerId;
    private List<OrderItem> orderItemsList;
    private Long totalAmount;
    private OrderStatus orderStatus;
    public OrderModel(){
        super();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<OrderItem> getOrderItemsList() {
        return orderItemsList;
    }

    public void setOrderItemsList(List<OrderItem> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public void setDefaultEntityProperties(Long createdById) {
        if(this.getCreatedTime() == null){
            /* means firstTime */
            this.setCreatedById(createdById);
            this.setCreatedTime(System.currentTimeMillis());
        }

        /* both times, this need to be set before saving or updating to db */
        this.setLastUpdatedById(createdById);
        this.setLastUpdatedTime(System.currentTimeMillis());
    }
}
