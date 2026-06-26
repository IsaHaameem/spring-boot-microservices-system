package com.microservices.order.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductLookupResponse(
        UUID id,
        String name,
        BigDecimal price,
        Integer stockQuantity
) {}