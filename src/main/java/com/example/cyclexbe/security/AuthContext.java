package com.example.cyclexbe.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthContext {
    private AuthContext() {}

    public static Integer getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }
        // vì principal = subject = userId string (theo JwtFilter của bạn)
        return Integer.parseInt(auth.getName());
    }

    public static String getRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return auth.getAuthorities().stream().findFirst().map(Object::toString).orElse(null);
    }
}
