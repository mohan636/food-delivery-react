package com.foodorder.kitchen.service;

import com.foodorder.kitchen.dto.KitchenRequest;
import com.foodorder.kitchen.dto.KitchenResponse;
import com.foodorder.kitchen.entity.KitchenTicket;
import com.foodorder.kitchen.entity.KitchenTicketStatus;
import com.foodorder.kitchen.exception.InvalidTicketException;
import com.foodorder.kitchen.repository.KitchenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class KitchenServiceImpl implements KitchenService {

    private static final Logger log = LoggerFactory.getLogger(KitchenServiceImpl.class);

    @Value("${order.service.url:http://localhost:8081/api/orders/}")
    private String orderServiceUrl;

    private final KitchenRepository kitchenRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public KitchenServiceImpl(KitchenRepository kitchenRepository, RestTemplate restTemplate) {
        this.kitchenRepository = kitchenRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public KitchenResponse createTicket(KitchenRequest request) {
        if (request.getOrderId() == null) {
            throw new InvalidTicketException("Order ID cannot be null");
        }
        if (request.getItemsDetail() == null || request.getItemsDetail().trim().isEmpty()) {
            throw new InvalidTicketException("Items detail cannot be empty");
        }

        // Verify that the order exists in order-service
        String checkUrl = orderServiceUrl + request.getOrderId();
        try {
            log.debug("Verifying order existence via: {}", checkUrl);
            restTemplate.getForObject(checkUrl, Object.class);
            log.info("[KitchenService] Order ID: {} verified successfully.", request.getOrderId());
        } catch (Exception e) {
            log.error("[KitchenService] Validation failed: Order ID: {} does not exist or order-service is down. Error: {}", 
                    request.getOrderId(), e.getMessage());
            throw new InvalidTicketException("Invalid order: Order ID " + request.getOrderId() + " does not exist or order-service is unavailable.");
        }

        // Generate unique ticket number (e.g. TCK-A8B9C2D3)
        String ticketNumber = "TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Populate Kitchen Ticket with status = READY
        KitchenTicket ticket = new KitchenTicket();
        ticket.setOrderId(request.getOrderId());
        ticket.setTicketNumber(ticketNumber);
        ticket.setItemsDetail(request.getItemsDetail());
        ticket.setStatus(KitchenTicketStatus.READY);

        KitchenTicket savedTicket = kitchenRepository.save(ticket);

        // Required log statement
        log.info("[KitchenService] Order #{} - Food READY (Ticket: {})", request.getOrderId(), ticketNumber);

        return new KitchenResponse(
                savedTicket.getId(),
                savedTicket.getOrderId(),
                savedTicket.getTicketNumber(),
                savedTicket.getStatus().name(),
                savedTicket.getItemsDetail());
    }
}
