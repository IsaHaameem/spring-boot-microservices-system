package com.microservices.user.service;

import com.microservices.user.dto.LoginRequest;
import com.microservices.user.dto.LoginResponse;
import com.microservices.user.dto.RegisterRequest;
import com.microservices.user.dto.UserResponse;
import com.microservices.user.entity.User;
import com.microservices.user.exception.EmailAlreadyExistsException;
import com.microservices.user.exception.InvalidCredentialsException;
import com.microservices.user.exception.UserNotFoundException;
import com.microservices.user.repository.UserRepository;
import com.microservices.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getCreatedAt()
        );
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail()
        );

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds()
        );
    }

    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}