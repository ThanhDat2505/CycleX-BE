package com.example.cyclexbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String method = request.getMethod();
        String uri = request.getRequestURI();

        log.info("[JwtFilter] {} {} | Auth header present: {}", method, uri, authHeader != null);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length()).trim();

            Optional<String> subjectOpt = jwtProvider.extractSubject(token);
            log.info("[JwtFilter] Token parsed - subject present: {}", subjectOpt.isPresent());

            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            boolean notYetAuthenticated = existingAuth == null
                    || !existingAuth.isAuthenticated()
                    || (existingAuth instanceof AnonymousAuthenticationToken);
            if (subjectOpt.isPresent() && notYetAuthenticated) {
                String subject = subjectOpt.get();
                String role = jwtProvider.extractRole(token).orElse("USER");

                log.info("[JwtFilter] userId={}, role={}", subject, role);

                // Spring Security convention: ROLE_*
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                        role.startsWith("ROLE_") ? role : "ROLE_" + role
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(subject, null, List.of(authority));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("[JwtFilter] Auth set: principal={}, authority={}", subject, authority.getAuthority());
            } else {
                log.warn("[JwtFilter] Token invalid or already authenticated. subjectPresent={}, notYetAuth={}", subjectOpt.isPresent(), notYetAuthenticated);
            }
        } else {
            log.warn("[JwtFilter] No Bearer token in request {} {}", method, uri);
        }

        filterChain.doFilter(request, response);
    }
}