package com.foodorder.order.workflow;

import com.foodorder.order.dto.OrderCreatedEvent;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorkflowStarter {

    private static final Logger log = LoggerFactory.getLogger(WorkflowStarter.class);

    private final RuntimeService runtimeService;

    @Autowired
    public WorkflowStarter(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public ProcessInstance startOrderWorkflow(OrderCreatedEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", event.getOrderId());
        variables.put("orderNumber", event.getOrderNumber());
        variables.put("customerName", event.getCustomerName());
        variables.put("amount", event.getAmount());
        variables.put("item", event.getItem());
        variables.put("deliveryAddress", event.getDeliveryAddress());

        // The process definition key matches the ID in order-process.bpmn
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "order-processing-workflow", 
                String.valueOf(event.getOrderId()), 
                variables
        );

        log.info("[OrderService] Order #{} - Camunda Workflow started with Instance ID: {}", 
                event.getOrderId(), processInstance.getId());
        return processInstance;
    }
}
