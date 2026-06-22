package com.foodorder.payment.service;

import com.foodorder.payment.dto.PaymentRequest;
import com.foodorder.payment.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
}
