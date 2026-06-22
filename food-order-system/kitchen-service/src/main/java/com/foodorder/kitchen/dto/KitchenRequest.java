package com.foodorder.kitchen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class KitchenRequest {
    @NotNull(message = "orderId is required")
    private Long orderId;

    @NotBlank(message = "itemsDetail is required")
    private String itemsDetail;

    public KitchenRequest() {
    }

    public KitchenRequest(Long orderId, String itemsDetail) {
        this.orderId = orderId;
        this.itemsDetail = itemsDetail;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getItemsDetail() {
        return itemsDetail;
    }

    public void setItemsDetail(String itemsDetail) {
        this.itemsDetail = itemsDetail;
    }
}
