package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.*;
import com.example.cyclexbe.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody UserCreateRequest req) {
        return authService.register(req);
    }

    @PostMapping("/verify-otp")
    public VerifyOtpResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {
        return authService.verifyOtp(req);
    }

    @PostMapping("/send-otp")
    public SendOtpResponse resendOtp(@Valid @RequestBody SendOtpRequest req) {
        return authService.sendOtp(req);
    }
}
