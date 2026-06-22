package com.foodorder.order.workflow.delegate;

import com.foodorder.order.entity.Order;
import com.foodorder.order.entity.OrderStatus;
import com.foodorder.order.repository.OrderRepository;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("kitchenDelegate")
public class KitchenDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(KitchenDelegate.class);

    @Value("${integration.kitchen.url}")
    private String kitchenServiceUrl;

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public KitchenDelegate(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // Update status to KITCHEN_PREPARATION
        order.setStatus(OrderStatus.KITCHEN_PREPARATION);
        orderRepository.save(order);

        log.info("[OrderService] Order #{} - Kitchen Preparation via {}", orderId, kitchenServiceUrl);

        try {
            KitchenRequest request = new KitchenRequest(orderId, order.getItem());
            log.debug("Sending kitchen ticket request to Kitchen Service for Order #{}, Item: {}", orderId, order.getItem());
            
            KitchenResponse response = restTemplate.postForObject(
                    kitchenServiceUrl,
                    request,
                    KitchenResponse.class
            );

            if (response != null && "READY".equalsIgnoreCase(response.getStatus())) {
                log.debug("Kitchen Ticket created successfully for Order #{}, Status: {}", orderId, response.getStatus());
            } else {
                throw new IllegalStateException("Kitchen ticket returned non-ready status: " + (response != null ? response.getStatus() : "null"));
            }
        } catch (Exception e) {
            log.error("Error communicating with Kitchen Service for Order #{}.", orderId, e);
            // Propagate BpmnError to trigger a Camunda Incident for operations to fix rather than silently proceeding!
            throw new BpmnError("KITCHEN_SERVICE_ERROR", "Failed to communicate with Kitchen service: " + e.getMessage());
        }
    }

    // Static nested DTOs for REST interaction with Kitchen Service
    public static class KitchenRequest {
        private Long orderId;
        private String itemsDetail;

        public KitchenRequest() {}

        public KitchenRequest(Long orderId, String itemsDetail) {
            this.orderId = orderId;
            this.itemsDetail = itemsDetail;
        }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getItemsDetail() { return itemsDetail; }
        public void setItemsDetail(String itemsDetail) { this.itemsDetail = itemsDetail; }
    }

    public static class KitchenResponse {
        private Long id;
        private Long orderId;
        private String ticketNumber;
        private String status;
        private String itemsDetail;

        public KitchenResponse() {}

        public KitchenResponse(Long id, Long orderId, String ticketNumber, String status, String itemsDetail) {
            this.id = id;
            this.orderId = orderId;
            this.ticketNumber = ticketNumber;
            this.status = status;
            this.itemsDetail = itemsDetail;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getTicketNumber() { return ticketNumber; }
        public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getItemsDetail() { return itemsDetail; }
        public void setItemsDetail(String itemsDetail) { this.itemsDetail = itemsDetail; }
    }
}
