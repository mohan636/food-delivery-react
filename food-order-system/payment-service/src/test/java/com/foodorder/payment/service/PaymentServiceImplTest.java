package com.foodorder.payment.service;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.foodorder.payment.dto.PaymentRequest;
import com.foodorder.payment.dto.PaymentResponse;
import com.foodorder.payment.entity.Payment;
import com.foodorder.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    public void testProcessPaymentLogic() {
        PaymentServiceImpl paymentService = new PaymentServiceImpl(paymentRepository, restTemplate);

        PaymentRequest request = new PaymentRequest(123L, new BigDecimal("100.00"));

        // Capture payment entity argument passed to the repository.save method
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        PaymentResponse response = paymentService.processPayment(request);

        // Verify that paymentRepository.save was called exactly once
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();

        // Assertions on saved entity
        assertEquals(123L, savedPayment.getOrderId());
        assertEquals(new BigDecimal("100.00"), savedPayment.getAmount());
        assertNotNull(savedPayment.getTransactionId());
        assertNotNull(savedPayment.getStatus());
        assertEquals("CREDIT_CARD", savedPayment.getPaymentMethod());

        // Assertions on response
        assertEquals(123L, response.getOrderId());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals(savedPayment.getTransactionId(), response.getTransactionId());
        assertNotNull(response.getStatus());

        // Verify status mapping consistency
        if ("SUCCESS".equals(response.getStatus())) {
            assertEquals("COMPLETED", savedPayment.getStatus().name());
        } else {
            assertEquals("FAILED", response.getStatus());
            assertEquals("FAILED", savedPayment.getStatus().name());
        }
    }
}
