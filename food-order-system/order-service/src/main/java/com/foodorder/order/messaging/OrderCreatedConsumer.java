package com.foodorder.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.foodorder.order.dto.OrderCreatedEvent;
import com.foodorder.order.workflow.WorkflowStarter;

import jakarta.annotation.PostConstruct;

@Component
@Profile("!local")
public class OrderCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    private final WorkflowStarter workflowStarter;

    @PostConstruct
    public void init() {
        System.out.println("OrderCreatedConsumer Loaded Successfully");
    }

    @Autowired
    public OrderCreatedConsumer(WorkflowStarter workflowStarter) {
        this.workflowStarter = workflowStarter;
    }

    @JmsListener(destination = "order.created")
    public void receiveOrderCreated(OrderCreatedEvent event) {

        System.out.println(
                "[ActiveMQ Consumer] Message Received for Order #"
                        + event.getOrderId());

        log.info(
                "[OrderService] Order #{} - Workflow started",
                event.getOrderId());

        try {
            workflowStarter.startOrderWorkflow(event);

            System.out.println(
                    "[Camunda] Workflow Started for Order #"
                            + event.getOrderId());

        } catch (Exception e) {

            log.error(
                    "Failed to trigger workflow starter for Order #{}",
                    event.getOrderId(),
                    e);
        }
    }
}
