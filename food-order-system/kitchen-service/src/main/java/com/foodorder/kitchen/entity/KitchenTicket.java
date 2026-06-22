package com.foodorder.kitchen.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kitchen_tickets")
public class KitchenTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "ticket_number", nullable = false, unique = true, length = 50)
    private String ticketNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private KitchenTicketStatus status;

    @Column(name = "items_detail", nullable = false, length = 500)
    private String itemsDetail;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Default Constructor
    public KitchenTicket() {
    }

    // Getters and Setters
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

    public KitchenTicketStatus getStatus() {
        return status;
    }

    public void setStatus(KitchenTicketStatus status) {
        this.status = status;
    }

    public String getItemsDetail() {
        return itemsDetail;
    }

    public void setItemsDetail(String itemsDetail) {
        this.itemsDetail = itemsDetail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
