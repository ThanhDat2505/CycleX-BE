package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.security.JwtProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        User user = userService.findByEmail(req.email);
        if (!passwordEncoder.matches(req.password, user.getPasswordHash())) {
            System.out.println("Invalid credentials");
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid credentials"
            );
        }
        LoginResponse response = new LoginResponse();
        response.accessToken = jwtProvider.generateToken(user);
        response.tokenType = "Bearer";
        response.user = UserResponse.from(user);
        return response;

    }

    public RegisterResponse register(@Valid @RequestBody UserCreateRequest req) {
        UserResponse user = userService.create(req);
        RegisterResponse response = new RegisterResponse();
        response.message = "Registration successful";
        response.user = user;
        return response;
    }
}
