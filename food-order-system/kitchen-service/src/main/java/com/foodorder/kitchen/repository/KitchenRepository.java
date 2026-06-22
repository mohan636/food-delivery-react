package com.foodorder.kitchen.repository;

import com.foodorder.kitchen.entity.KitchenTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KitchenRepository extends JpaRepository<KitchenTicket, Long> {
    Optional<KitchenTicket> findByOrderId(Long orderId);
    Optional<KitchenTicket> findByTicketNumber(String ticketNumber);
}
