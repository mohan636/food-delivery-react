package com.foodorder.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderCreatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String orderNumber;
    private String customerName;
    private String item;
    private BigDecimal amount;
    private String deliveryAddress;
    private String status;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Long orderId, String orderNumber, String customerName, String item, 
                             BigDecimal amount, String deliveryAddress, String status) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.item = item;
        this.amount = amount;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
