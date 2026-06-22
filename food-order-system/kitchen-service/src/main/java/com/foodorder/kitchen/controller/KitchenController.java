package com.foodorder.kitchen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.kitchen.dto.KitchenRequest;
import com.foodorder.kitchen.dto.KitchenResponse;
import com.foodorder.kitchen.service.KitchenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/kitchen/tickets")
public class KitchenController {

    private final KitchenService kitchenService;

    @Autowired
    public KitchenController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @PostMapping
    public ResponseEntity<KitchenResponse> createTicket(@Valid @RequestBody KitchenRequest request) {
        KitchenResponse response = kitchenService.createTicket(request);
        return ResponseEntity.ok(response);
    }
}
