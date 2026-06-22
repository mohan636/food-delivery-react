package com.foodorder.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeliveryRequest {
    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotBlank(message = "deliveryAddress is required")
    private String deliveryAddress;

    public DeliveryRequest() {
    }

    public DeliveryRequest(Long orderId, String deliveryAddress) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
