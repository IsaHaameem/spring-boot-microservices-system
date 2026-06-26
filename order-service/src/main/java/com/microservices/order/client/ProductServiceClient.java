package com.microservices.order.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PatchExchange;

import java.util.UUID;

public interface ProductServiceClient {

    @GetExchange("/api/products/{id}")
    ProductLookupResponse getProduct(@PathVariable UUID id);

    @PatchExchange("/internal/products/{id}/reduce-stock")
    void reduceStock(@PathVariable UUID id, @RequestBody ReduceStockRequest request);

}