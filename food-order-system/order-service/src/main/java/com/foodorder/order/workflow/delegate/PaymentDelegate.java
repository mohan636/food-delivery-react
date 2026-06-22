package com.foodorder.order.workflow.delegate;

import com.foodorder.order.entity.Order;
import com.foodorder.order.entity.OrderStatus;
import com.foodorder.order.repository.OrderRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component("paymentDelegate")
public class PaymentDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(PaymentDelegate.class);

    @Value("${integration.payment.url}")
    private String paymentServiceUrl;

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public PaymentDelegate(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // Update status to PAYMENT_PROCESSING
        order.setStatus(OrderStatus.PAYMENT_PROCESSING);
        orderRepository.save(order);

        log.info("[OrderService] Order #{} - Payment Processing via {}", orderId, paymentServiceUrl);

        boolean paymentSuccess = false;
        try {
            PaymentRequest request = new PaymentRequest(orderId, order.getAmount());
            log.debug("Sending payment request to Payment Service for Order #{}, Amount: {}", orderId, order.getAmount());
            
            PaymentResponse response = restTemplate.postForObject(
                    paymentServiceUrl,
                    request,
                    PaymentResponse.class
            );

            if (response != null && "SUCCESS".equalsIgnoreCase(response.getStatus())) {
                paymentSuccess = true;
            }
        } catch (Exception e) {
            log.error("Error communicating with Payment Service for Order #{}", orderId, e);
        }

        execution.setVariable("paymentSuccess", paymentSuccess);
        log.debug("Payment result for Order #{}: {}", orderId, paymentSuccess ? "SUCCESS" : "FAILED");
    }

    // Static nested DTOs for REST interaction with Payment Service
    public static class PaymentRequest {
        private Long orderId;
        private BigDecimal amount;

        public PaymentRequest() {}

        public PaymentRequest(Long orderId, BigDecimal amount) {
            this.orderId = orderId;
            this.amount = amount;
        }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public static class PaymentResponse {
        private String transactionId;
        private Long orderId;
        private BigDecimal amount;
        private String status;

        public PaymentResponse() {}

        public PaymentResponse(String transactionId, Long orderId, BigDecimal amount, String status) {
            this.transactionId = transactionId;
            this.orderId = orderId;
            this.amount = amount;
            this.status = status;
        }

        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
