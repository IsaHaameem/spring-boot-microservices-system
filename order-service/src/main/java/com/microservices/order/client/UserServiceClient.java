package com.microservices.order.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.UUID;

public interface UserServiceClient {

    @GetExchange("/internal/users/{id}")
    UserLookupResponse getUser(@PathVariable UUID id);

}