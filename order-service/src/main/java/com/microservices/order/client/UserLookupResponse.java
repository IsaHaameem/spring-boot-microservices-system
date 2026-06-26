package com.microservices.order.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserLookupResponse(
        UUID id,
        String fullName,
        String email
) {}