package com.microservices.product.controller;

import com.microservices.product.dto.CreateProductRequest;
import com.microservices.product.dto.ProductResponse;
import com.microservices.product.dto.ReduceStockRequest;
import com.microservices.product.dto.UpdateProductRequest;
import com.microservices.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/api/products")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @GetMapping("/api/products")
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @GetMapping("/api/products/{id}")
    public ProductResponse findById(@PathVariable UUID id) {
        return productService.findById(id);
    }

    @PutMapping("/api/products/{id}")
    public ProductResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/api/products/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/internal/products/{id}/reduce-stock")
    public ResponseEntity<Void> reduceStock(@PathVariable UUID id, @Valid @RequestBody ReduceStockRequest request) {
        productService.reduceStock(id, request.quantity());
        return ResponseEntity.noContent().build();
    }

}