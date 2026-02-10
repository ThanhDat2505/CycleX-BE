package com.example.cyclexbe.controller;

import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.UserRepository;
import com.example.cyclexbe.security.JwtProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/dev")
public class DevAuthController {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public DevAuthController(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/token/{userId}")
    public Map<String, String> token(@PathVariable Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String t = jwtProvider.generateToken(user);
        return Map.of("token", t);
    }
}
