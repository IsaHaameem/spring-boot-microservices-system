package com.microservices.product.service;

import com.microservices.product.dto.CreateProductRequest;
import com.microservices.product.dto.ProductResponse;
import com.microservices.product.dto.UpdateProductRequest;
import com.microservices.product.entity.Product;
import com.microservices.product.exception.InsufficientStockException;
import com.microservices.product.exception.ProductNotFoundException;
import com.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse create(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());

        return toResponse(productRepository.save(product));
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse findById(UUID id) {
        return toResponse(getOrThrow(id));
    }

    public ProductResponse update(UUID id, UpdateProductRequest request) {
        Product product = getOrThrow(id);

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());

        return toResponse(productRepository.save(product));
    }

    public void delete(UUID id) {
        productRepository.delete(getOrThrow(id));
    }

    @org.springframework.transaction.annotation.Transactional
    public void reduceStock(UUID id, int quantity) {
        int updated = productRepository.decrementStock(id, quantity);

        if (updated == 0) {
            if (!productRepository.existsById(id)) {
                throw new ProductNotFoundException(id);
            }

            throw new InsufficientStockException(id);
        }
    }

    private Product getOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}