package com.foodorder.order.entity;

public enum OrderStatus {
    PLACED,
    PAYMENT_PROCESSING,
    KITCHEN_PREPARATION,
    OUT_FOR_DELIVERY,
    DELIVERED,
    PAYMENT_FAILED,
    CANCELLED
}
