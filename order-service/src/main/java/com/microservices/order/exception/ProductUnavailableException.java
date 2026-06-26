package com.microservices.order.exception;

import java.util.UUID;

public class ProductUnavailableException extends RuntimeException {

    public ProductUnavailableException(UUID productId) {
        super("Product not found: " + productId);
    }

}