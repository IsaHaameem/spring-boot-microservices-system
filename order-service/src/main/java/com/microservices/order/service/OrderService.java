package com.microservices.order.service;

import com.microservices.order.client.ProductLookupResponse;
import com.microservices.order.client.ProductServiceClient;
import com.microservices.order.client.ReduceStockRequest;
import com.microservices.order.client.UserServiceClient;
import com.microservices.order.dto.CreateOrderRequest;
import com.microservices.order.dto.OrderItemRequest;
import com.microservices.order.dto.OrderItemResponse;
import com.microservices.order.dto.OrderResponse;
import com.microservices.order.entity.Order;
import com.microservices.order.entity.OrderItem;
import com.microservices.order.entity.OrderStatus;
import com.microservices.order.exception.InsufficientStockException;
import com.microservices.order.exception.ProductUnavailableException;
import com.microservices.order.exception.UserNotFoundException;
import com.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;

    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {

        // Confirm the user is real before doing anything else.
        try {
            userServiceClient.getUser(userId);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new UserNotFoundException(userId);
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        // Validate + price-snapshot every item. This stock check is a GET —
        // it's advisory, meant to fail fast with a friendly error. The real
        // enforcement happens atomically below, in reduceStock().
        for (OrderItemRequest itemRequest : request.items()) {
            ProductLookupResponse product;
            try {
                product = productServiceClient.getProduct(itemRequest.productId());
            } catch (HttpClientErrorException.NotFound ex) {
                throw new ProductUnavailableException(itemRequest.productId());
            }

            if (product.stockQuantity() < itemRequest.quantity()) {
                throw new InsufficientStockException(itemRequest.productId());
            }

            OrderItem item = new OrderItem();
            item.setProductId(product.id());
            item.setProductName(product.name());
            item.setUnitPrice(product.price());
            item.setQuantity(itemRequest.quantity());
            order.addItem(item);

            total = total.add(product.price().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }

        order.setTotalAmount(total);
        order = orderRepository.save(order);

        // Reserve the stock for real. THIS is the unsafe window described in
        // the chat: between the check above and this call, a concurrent order
        // could have consumed the same stock. If this fails partway through a
        // multi-item order, earlier items in THIS loop already had their
        // stock decremented for an order that's about to be marked FAILED —
        // and nothing here puts that stock back. The correct fix is the Saga
        // pattern (compensating transactions via a message broker), which is
        // deliberately not built in this project.
        try {
            for (OrderItem item : order.getItems()) {
                productServiceClient.reduceStock(item.getProductId(), new ReduceStockRequest(item.getQuantity()));
            }
            order.setStatus(OrderStatus.CONFIRMED);
        } catch (HttpClientErrorException ex) {
            order.setStatus(OrderStatus.FAILED);
        }

        order = orderRepository.save(order);

        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getUnitPrice(),
                                item.getQuantity(),
                                item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                        ))
                        .toList(),
                order.getCreatedAt()
        );
    }

}