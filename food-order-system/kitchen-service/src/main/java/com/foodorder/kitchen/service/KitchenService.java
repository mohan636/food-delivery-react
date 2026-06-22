package com.foodorder.kitchen.service;

import com.foodorder.kitchen.dto.KitchenRequest;
import com.foodorder.kitchen.dto.KitchenResponse;

public interface KitchenService {
    KitchenResponse createTicket(KitchenRequest request);
}
