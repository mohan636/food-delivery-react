package com.foodorder.delivery.service;

import com.foodorder.delivery.dto.DeliveryRequest;
import com.foodorder.delivery.dto.DeliveryResponse;
import com.foodorder.delivery.entity.Delivery;
import com.foodorder.delivery.entity.DeliveryStatus;
import com.foodorder.delivery.repository.DeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private static final Logger log =
            LoggerFactory.getLogger(DeliveryServiceImpl.class);

    private final DeliveryRepository deliveryRepository;

    public DeliveryServiceImpl(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {

        Delivery delivery = new Delivery();

        delivery.setOrderId(request.getOrderId());
        delivery.setDeliveryAddress(request.getDeliveryAddress());

        // Mock courier assignment
        delivery.setCourierName("Courier-001");

        // Initial status
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        Delivery saved = deliveryRepository.save(delivery);

        log.info("[DeliveryService] Order #{} - ASSIGNED",
                saved.getOrderId());

        return new DeliveryResponse(
                saved.getOrderId(),
                saved.getCourierName(),
                saved.getStatus().name()
        );
    }
}