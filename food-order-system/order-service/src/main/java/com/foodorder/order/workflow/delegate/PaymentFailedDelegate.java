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

@Component("paymentFailedDelegate")
public class PaymentFailedDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(PaymentFailedDelegate.class);

    private final OrderRepository orderRepository;

    @Autowired
    public PaymentFailedDelegate(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        order.setStatus(OrderStatus.PAYMENT_FAILED);
        orderRepository.save(order);

        log.info("[OrderService] Order #{} - Payment Failed", orderId);
    }
}
