package com.foodorder.order.messaging;

import com.foodorder.order.dto.OrderCreatedEvent;
import org.springframework.context.ApplicationEvent;

public class OrderCreatedSpringEvent extends ApplicationEvent {
    private final OrderCreatedEvent event;

    public OrderCreatedSpringEvent(Object source, OrderCreatedEvent event) {
        super(source);
        this.event = event;
    }

    public OrderCreatedEvent getEvent() {
        return event;
    }
}
