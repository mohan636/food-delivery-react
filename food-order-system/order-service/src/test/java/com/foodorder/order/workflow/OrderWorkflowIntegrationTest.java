package com.foodorder.order.workflow;

import com.foodorder.order.dto.OrderCreatedEvent;
import com.foodorder.order.entity.Order;
import com.foodorder.order.entity.OrderStatus;
import com.foodorder.order.messaging.OrderCreatedConsumer;
import com.foodorder.order.repository.OrderRepository;
import com.foodorder.order.workflow.delegate.*;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class OrderWorkflowIntegrationTest {

    @Mock
    private RuntimeService runtimeService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProcessInstance processInstance;

    @Mock
    private DelegateExecution execution;

    @Test
    public void testConsumerTriggersWorkflowStarter() {
        WorkflowStarter workflowStarter = new WorkflowStarter(runtimeService);
        OrderCreatedConsumer consumer = new OrderCreatedConsumer(workflowStarter);

        OrderCreatedEvent event = new OrderCreatedEvent(
                123L, "ORD-A8B9C2D3", "Jane Doe", "Burger",
                new BigDecimal("45.50"), "456 Elm St", "PLACED"
        );

        when(runtimeService.startProcessInstanceByKey(
                eq("order-processing-workflow"), 
                eq("123"), 
                anyMap()
        )).thenReturn(processInstance);
        
        when(processInstance.getId()).thenReturn("test-process-instance-id");

        consumer.receiveOrderCreated(event);

        verify(runtimeService).startProcessInstanceByKey(
                eq("order-processing-workflow"), 
                eq("123"), 
                anyMap()
        );
    }

    @Test
    public void testPaymentDelegateSuccessfulPath() throws Exception {
        PaymentDelegate delegate = new PaymentDelegate(orderRepository, restTemplate);

        Order order = new Order();
        order.setId(456L);
        order.setStatus(OrderStatus.PLACED);
        order.setAmount(new BigDecimal("150.00"));

        when(execution.getVariable("orderId")).thenReturn(456L);
        when(orderRepository.findById(456L)).thenReturn(Optional.of(order));
        
        PaymentDelegate.PaymentResponse mockResponse = new PaymentDelegate.PaymentResponse(
                "TXN-123", 456L, new BigDecimal("150.00"), "SUCCESS"
        );
        when(restTemplate.postForObject(nullable(String.class), any(), eq(PaymentDelegate.PaymentResponse.class)))
                .thenReturn(mockResponse);

        delegate.execute(execution);

        assertEquals(OrderStatus.PAYMENT_PROCESSING, order.getStatus());
        verify(orderRepository).save(order);
        verify(execution).setVariable("paymentSuccess", true);
    }

    @Test
    public void testPaymentDelegateFailedPath() throws Exception {
        PaymentDelegate delegate = new PaymentDelegate(orderRepository, restTemplate);

        Order order = new Order();
        order.setId(789L);
        order.setStatus(OrderStatus.PLACED);
        order.setAmount(new BigDecimal("150.00"));

        when(execution.getVariable("orderId")).thenReturn(789L);
        when(orderRepository.findById(789L)).thenReturn(Optional.of(order));
        
        PaymentDelegate.PaymentResponse mockResponse = new PaymentDelegate.PaymentResponse(
                "TXN-123", 789L, new BigDecimal("150.00"), "FAILED"
        );
        when(restTemplate.postForObject(nullable(String.class), any(), eq(PaymentDelegate.PaymentResponse.class)))
                .thenReturn(mockResponse);

        delegate.execute(execution);

        assertEquals(OrderStatus.PAYMENT_PROCESSING, order.getStatus());
        verify(orderRepository).save(order);
        verify(execution).setVariable("paymentSuccess", false);
    }

    @Test
    public void testPaymentDelegateExceptionPath() throws Exception {
        PaymentDelegate delegate = new PaymentDelegate(orderRepository, restTemplate);

        Order order = new Order();
        order.setId(999L);
        order.setStatus(OrderStatus.PLACED);
        order.setAmount(new BigDecimal("1250.00"));

        when(execution.getVariable("orderId")).thenReturn(999L);
        when(orderRepository.findById(999L)).thenReturn(Optional.of(order));
        
        when(restTemplate.postForObject(nullable(String.class), any(), eq(PaymentDelegate.PaymentResponse.class)))
                .thenThrow(new org.springframework.web.client.RestClientException("Connection Refused"));

        delegate.execute(execution);

        assertEquals(OrderStatus.PAYMENT_PROCESSING, order.getStatus());
        verify(orderRepository).save(order);
        verify(execution).setVariable("paymentSuccess", false);
    }

    @Test
    public void testKitchenDelegate() throws Exception {
        KitchenDelegate delegate = new KitchenDelegate(orderRepository, restTemplate);

        Order order = new Order();
        order.setId(123L);
        order.setStatus(OrderStatus.PAYMENT_PROCESSING);
        order.setItem("Pizza");

        when(execution.getVariable("orderId")).thenReturn(123L);
        when(orderRepository.findById(123L)).thenReturn(Optional.of(order));

        KitchenDelegate.KitchenResponse mockResponse = new KitchenDelegate.KitchenResponse(
                1L, 123L, "TCK-123", "READY", "Pizza"
        );
        when(restTemplate.postForObject(nullable(String.class), any(), eq(KitchenDelegate.KitchenResponse.class)))
                .thenReturn(mockResponse);

        delegate.execute(execution);

        assertEquals(OrderStatus.KITCHEN_PREPARATION, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    public void testDeliveryDelegate() throws Exception {
        DeliveryDelegate delegate = new DeliveryDelegate(orderRepository, restTemplate);

        Order order = new Order();
        order.setId(123L);
        order.setStatus(OrderStatus.KITCHEN_PREPARATION);

        when(execution.getVariable("orderId")).thenReturn(123L);
        when(orderRepository.findById(123L)).thenReturn(Optional.of(order));

        DeliveryDelegate.DeliveryResponse mockResponse = new DeliveryDelegate.DeliveryResponse(
                123L, "Courier-1", "ASSIGNED"
        );
        when(restTemplate.postForObject(nullable(String.class), any(), eq(DeliveryDelegate.DeliveryResponse.class)))
                .thenReturn(mockResponse);

        delegate.execute(execution);

        assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    public void testDeliveredDelegate() throws Exception {
        DeliveredDelegate delegate = new DeliveredDelegate(orderRepository);

        Order order = new Order();
        order.setId(123L);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);

        when(execution.getVariable("orderId")).thenReturn(123L);
        when(orderRepository.findById(123L)).thenReturn(Optional.of(order));

        delegate.execute(execution);

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    public void testPaymentFailedDelegate() throws Exception {
        PaymentFailedDelegate delegate = new PaymentFailedDelegate(orderRepository);

        Order order = new Order();
        order.setId(789L);
        order.setStatus(OrderStatus.PAYMENT_PROCESSING);

        when(execution.getVariable("orderId")).thenReturn(789L);
        when(orderRepository.findById(789L)).thenReturn(Optional.of(order));

        delegate.execute(execution);

        assertEquals(OrderStatus.PAYMENT_FAILED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    public void testCancelledDelegate() throws Exception {
        CancelledDelegate delegate = new CancelledDelegate(orderRepository);

        Order order = new Order();
        order.setId(789L);
        order.setStatus(OrderStatus.PAYMENT_FAILED);

        when(execution.getVariable("orderId")).thenReturn(789L);
        when(orderRepository.findById(789L)).thenReturn(Optional.of(order));

        delegate.execute(execution);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }
}
