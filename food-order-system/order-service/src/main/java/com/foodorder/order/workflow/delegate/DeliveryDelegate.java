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

@Component("deliveryDelegate")
public class DeliveryDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(DeliveryDelegate.class);

    @Value("${integration.delivery.url}")
    private String deliveryServiceUrl;

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public DeliveryDelegate(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);

        log.info("[OrderService] Order #{} - Out for Delivery. Triggering Courier Rider Assignment via {}", orderId, deliveryServiceUrl);

        try {
            DeliveryRequest request = new DeliveryRequest(orderId, order.getDeliveryAddress());
            
            DeliveryResponse response = restTemplate.postForObject(
                    deliveryServiceUrl,
                    request,
                    DeliveryResponse.class
            );

            if (response != null) {
                log.info("[OrderService] Courier assigned for Order #{}. Rider: {}, Status: {}", 
                        orderId, response.getCourierName(), response.getStatus());
            }
        } catch (Exception e) {
            log.error("Failed to assign courier rider for Order #{} at delivery-service", orderId, e);
            throw new BpmnError("DELIVERY_SERVICE_ERROR", "Failed to communicate with Delivery service: " + e.getMessage());
        }
    }

    // Static nested DTOs for REST interaction with Delivery Service
    public static class DeliveryRequest {
        private Long orderId;
        private String deliveryAddress;

        public DeliveryRequest() {}

        public DeliveryRequest(Long orderId, String deliveryAddress) {
            this.orderId = orderId;
            this.deliveryAddress = deliveryAddress;
        }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    }

    public static class DeliveryResponse {
        private Long orderId;
        private String courierName;
        private String status;

        public DeliveryResponse() {}

        public DeliveryResponse(Long orderId, String courierName, String status) {
            this.orderId = orderId;
            this.courierName = courierName;
            this.status = status;
        }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getCourierName() { return courierName; }
        public void setCourierName(String courierName) { this.courierName = courierName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
