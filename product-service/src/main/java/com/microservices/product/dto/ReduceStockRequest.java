package com.microservices.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReduceStockRequest(

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Integer quantity

) {}