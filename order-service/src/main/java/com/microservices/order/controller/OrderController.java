package com.microservices.order.controller;

import com.microservices.order.dto.CreateOrderRequest;
import com.microservices.order.dto.OrderResponse;
import com.microservices.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}