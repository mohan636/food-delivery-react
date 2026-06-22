package com.foodorder.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.foodorder.order.workflow.WorkflowStarter;

@Component
@Profile("local")
public class LocalOrderCreatedEventListener {

    private static final Logger log = LoggerFactory.getLogger(LocalOrderCreatedEventListener.class);

    private final WorkflowStarter workflowStarter;

    @Autowired
    public LocalOrderCreatedEventListener(WorkflowStarter workflowStarter) {
        this.workflowStarter = workflowStarter;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedDirectly(OrderCreatedSpringEvent wrapperEvent) {
        log.info("[LocalOrderCreatedEventListener] ActiveMQ is disabled/local profile active. Starting Camunda workflow directly for Order #{}", 
                wrapperEvent.getEvent().getOrderId());
        
        try {
            workflowStarter.startOrderWorkflow(wrapperEvent.getEvent());
            log.info("[LocalOrderCreatedEventListener] Successfully triggered workflow starter for Order #{}", 
                    wrapperEvent.getEvent().getOrderId());
        } catch (Exception e) {
            log.error("[LocalOrderCreatedEventListener] Failed to start workflow directly for Order #{}", 
                    wrapperEvent.getEvent().getOrderId(), e);
        }
    }
}
