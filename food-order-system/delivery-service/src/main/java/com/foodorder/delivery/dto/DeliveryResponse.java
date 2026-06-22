package com.foodorder.delivery.dto;

public class DeliveryResponse {

    private Long orderId;
    private String courierName;
    private String status;

    public DeliveryResponse() {
    }

    public DeliveryResponse(Long orderId, String courierName, String status) {
        this.orderId = orderId;
        this.courierName = courierName;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
