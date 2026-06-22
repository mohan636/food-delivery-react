package com.foodorder.order.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodorder.order.dto.OrderCreatedEvent;
import com.foodorder.order.dto.OrderRequest;
import com.foodorder.order.dto.OrderResponse;
import com.foodorder.order.entity.Order;
import com.foodorder.order.entity.OrderStatus;
import com.foodorder.order.exception.ResourceNotFoundException;
import com.foodorder.order.messaging.OrderCreatedSpringEvent;
import com.foodorder.order.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("[OrderService] Create order request received for customer: {}", orderRequest.getCustomerName());
        
        // 1. Generate unique order number (e.g. ORD-A8B9C2D3)
        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. Create and set order properties
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setCustomerName(orderRequest.getCustomerName() != null ? orderRequest.getCustomerName().trim() : "Unknown");
        order.setItem(orderRequest.getItem() != null ? orderRequest.getItem().trim() : "None");
        order.setDeliveryAddress(
                orderRequest.getDeliveryAddress() == null || orderRequest.getDeliveryAddress().isBlank()
                        ? "Address not provided"
                        : orderRequest.getDeliveryAddress().trim());
        order.setAmount(orderRequest.getAmount());
        order.setStatus(OrderStatus.PLACED);

        // 3. Save order in MySQL
        Order savedOrder = orderRepository.save(order);

        // 4. Log confirmation
        log.info("[OrderService] Order #{} - PLACED", savedOrder.getId());

        // 5. Construct event payload
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getCustomerName(),
                savedOrder.getItem(),
                savedOrder.getAmount(),
                savedOrder.getDeliveryAddress(),
                savedOrder.getStatus().name());

        // 6. Publish Spring Event (Listener publishes to ActiveMQ after commit)
        log.debug("[OrderService] Publishing internal Spring Event for Order: {}", orderNumber);
        eventPublisher.publishEvent(new OrderCreatedSpringEvent(this, event));

        // 7. Return mapped DTO response
        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getItem(),
                order.getDeliveryAddress(),
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt());
    }
}
