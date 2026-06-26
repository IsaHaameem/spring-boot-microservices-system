package com.microservices.user.controller;

import com.microservices.user.dto.UserResponse;
import com.microservices.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/users/me")
    public Map<String, String> me(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Email") String email) {
        return Map.of("userId", userId, "email", email);
    }

    @GetMapping("/internal/users/{id}")
    public UserResponse findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

}