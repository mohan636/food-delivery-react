package com.foodorder.delivery.service;

import com.foodorder.delivery.dto.DeliveryRequest;
import com.foodorder.delivery.dto.DeliveryResponse;

public interface DeliveryService {

    DeliveryResponse createDelivery(DeliveryRequest request);
}
