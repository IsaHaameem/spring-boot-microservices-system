package com.microservices.user.dto;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds
) {}