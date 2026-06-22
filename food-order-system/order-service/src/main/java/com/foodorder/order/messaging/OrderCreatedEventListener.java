package com.foodorder.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Profile("!local")
public class OrderCreatedEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventListener.class);
    
    private final JmsTemplate jmsTemplate;

    @Autowired
    public OrderCreatedEventListener(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedAfterCommit(OrderCreatedSpringEvent wrapperEvent) {
        log.info("[OrderCreatedEventListener] Transaction committed. Safe to publish event to ActiveMQ.");
        
        try {
            jmsTemplate.convertAndSend("order.created", wrapperEvent.getEvent());
            log.info("[OrderCreatedEventListener] Successfully published OrderCreatedEvent to ActiveMQ for Order ID: {}", 
                    wrapperEvent.getEvent().getOrderId());
        } catch (Exception e) {
            log.error("[OrderCreatedEventListener] Failed to publish OrderCreatedEvent to ActiveMQ for Order ID: {}", 
                    wrapperEvent.getEvent().getOrderId(), e);
        }
    }
}
