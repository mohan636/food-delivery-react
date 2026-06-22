package com.foodorder.kitchen.dto;

public class KitchenResponse {
    private Long id;
    private Long orderId;
    private String ticketNumber;
    private String status;
    private String itemsDetail;

    public KitchenResponse() {
    }

    public KitchenResponse(Long id, Long orderId, String ticketNumber, String status, String itemsDetail) {
        this.id = id;
        this.orderId = orderId;
        this.ticketNumber = ticketNumber;
        this.status = status;
        this.itemsDetail = itemsDetail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemsDetail() {
        return itemsDetail;
    }

    public void setItemsDetail(String itemsDetail) {
        this.itemsDetail = itemsDetail;
    }
}
