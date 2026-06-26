package com.microservices.product.repository;

import com.microservices.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity, p.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE p.id = :id AND p.stockQuantity >= :quantity")
    int decrementStock(@Param("id") UUID id, @Param("quantity") int quantity);

}