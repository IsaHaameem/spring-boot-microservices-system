package com.microservices.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        String status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        Instant createdAt
) {}