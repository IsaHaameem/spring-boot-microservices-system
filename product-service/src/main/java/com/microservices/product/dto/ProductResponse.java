package com.microservices.product.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        Instant createdAt,
        Instant updatedAt
) {}