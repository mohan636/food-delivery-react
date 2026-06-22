package com.foodorder.order.service;

import com.foodorder.order.dto.OrderRequest;
import com.foodorder.order.dto.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long id);
}
