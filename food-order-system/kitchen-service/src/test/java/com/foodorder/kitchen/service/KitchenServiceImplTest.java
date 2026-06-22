package com.foodorder.kitchen.service;

import com.foodorder.kitchen.dto.KitchenRequest;
import com.foodorder.kitchen.dto.KitchenResponse;
import com.foodorder.kitchen.entity.KitchenTicket;
import com.foodorder.kitchen.entity.KitchenTicketStatus;
import com.foodorder.kitchen.exception.InvalidTicketException;
import com.foodorder.kitchen.repository.KitchenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class KitchenServiceImplTest {

    @Mock
    private KitchenRepository kitchenRepository;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void testCreateTicketSuccess() {
        KitchenServiceImpl kitchenService = new KitchenServiceImpl(kitchenRepository, restTemplate);

        KitchenRequest request = new KitchenRequest(123L, "1x Pizza, 2x Soda");

        ArgumentCaptor<KitchenTicket> ticketCaptor = ArgumentCaptor.forClass(KitchenTicket.class);

        // Mock RestTemplate to verify that order check succeeds
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());

        // Mock repository save behavior to return the passed ticket (modified to have an ID)
        when(kitchenRepository.save(any(KitchenTicket.class))).thenAnswer(invocation -> {
            KitchenTicket ticket = invocation.getArgument(0);
            ticket.setId(1L);
            return ticket;
        });

        KitchenResponse response = kitchenService.createTicket(request);

        verify(kitchenRepository, times(1)).save(ticketCaptor.capture());

        KitchenTicket savedTicket = ticketCaptor.getValue();
        assertEquals(123L, savedTicket.getOrderId());
        assertEquals("1x Pizza, 2x Soda", savedTicket.getItemsDetail());
        assertEquals(KitchenTicketStatus.READY, savedTicket.getStatus());
        assertNotNull(savedTicket.getTicketNumber());
        assertTrue(savedTicket.getTicketNumber().startsWith("TCK-"));

        assertEquals(1L, response.getId());
        assertEquals(123L, response.getOrderId());
        assertEquals(savedTicket.getTicketNumber(), response.getTicketNumber());
        assertEquals("READY", response.getStatus());
        assertEquals("1x Pizza, 2x Soda", response.getItemsDetail());
    }

    @Test
    public void testCreateTicketNullOrderIdThrowsException() {
        KitchenServiceImpl kitchenService = new KitchenServiceImpl(kitchenRepository, restTemplate);

        KitchenRequest request = new KitchenRequest(null, "1x Pizza");

        InvalidTicketException exception = assertThrows(InvalidTicketException.class, () -> {
            kitchenService.createTicket(request);
        });

        assertEquals("Order ID cannot be null", exception.getMessage());
        verify(kitchenRepository, never()).save(any());
    }

    @Test
    public void testCreateTicketEmptyItemsDetailThrowsException() {
        KitchenServiceImpl kitchenService = new KitchenServiceImpl(kitchenRepository, restTemplate);

        KitchenRequest request = new KitchenRequest(123L, "   ");

        InvalidTicketException exception = assertThrows(InvalidTicketException.class, () -> {
            kitchenService.createTicket(request);
        });

        assertEquals("Items detail cannot be empty", exception.getMessage());
        verify(kitchenRepository, never()).save(any());
    }
}
