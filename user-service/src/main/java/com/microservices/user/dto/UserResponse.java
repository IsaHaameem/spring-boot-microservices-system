package com.microservices.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        Instant createdAt
) {}