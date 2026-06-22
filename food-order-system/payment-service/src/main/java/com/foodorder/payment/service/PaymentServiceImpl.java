package com.foodorder.payment.service;

import com.foodorder.payment.dto.PaymentRequest;
import com.foodorder.payment.dto.PaymentResponse;
import com.foodorder.payment.entity.Payment;
import com.foodorder.payment.entity.PaymentStatus;
import com.foodorder.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${order.service.url:http://localhost:8081/api/orders/}")
    private String orderServiceUrl;

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("[PaymentService] Processing payment request for Order ID: {}", request.getOrderId());

        // Verify that the order exists in order-service
        String checkUrl = orderServiceUrl + request.getOrderId();
        try {
            log.debug("Verifying order existence via: {}", checkUrl);
            restTemplate.getForObject(checkUrl, Object.class);
            log.info("[PaymentService] Order ID: {} verified successfully.", request.getOrderId());
        } catch (Exception e) {
            log.error("[PaymentService] Validation failed: Order ID: {} does not exist or order-service is down. Error: {}", 
                    request.getOrderId(), e.getMessage());
            throw new IllegalArgumentException("Invalid order: Order ID " + request.getOrderId() + " does not exist or order-service is unavailable.");
        }

        // Generate a unique transaction ID (e.g. TXN-A8B9C2D3)
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 80% success rate simulation
        double randomVal = Math.random();
        boolean isSuccess = randomVal < 0.8;

        PaymentStatus status = isSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;

        // Populate and save Payment record
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setTransactionId(transactionId);
        payment.setAmount(request.getAmount());
        payment.setStatus(status);
        payment.setPaymentMethod("CREDIT_CARD");

        paymentRepository.save(payment);

        if (isSuccess) {
            log.info("[PaymentService] Order #{} - Payment SUCCESS (TXN: {})", request.getOrderId(), transactionId);
        } else {
            log.warn("[PaymentService] Order #{} - Payment FAILED (TXN: {})", request.getOrderId(), transactionId);
        }

        String responseStatus = isSuccess ? "SUCCESS" : "FAILED";
        return new PaymentResponse(
                transactionId,
                request.getOrderId(),
                request.getAmount(),
                responseStatus
        );
    }
}
