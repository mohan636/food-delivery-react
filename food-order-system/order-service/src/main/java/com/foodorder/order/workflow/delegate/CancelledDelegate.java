package com.foodorder.order.workflow.delegate;

import com.foodorder.order.entity.Order;
import com.foodorder.order.entity.OrderStatus;
import com.foodorder.order.repository.OrderRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("cancelledDelegate")
public class CancelledDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CancelledDelegate.class);

    private final OrderRepository orderRepository;

    @Autowired
    public CancelledDelegate(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        log.info("[OrderService] Order #{} - Cancelled", orderId);
    }
}
