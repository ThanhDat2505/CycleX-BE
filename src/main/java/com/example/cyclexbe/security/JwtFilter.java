package com.example.cyclexbe.security;

import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public JwtFilter(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        System.out.println("[JWT FILTER] " + request.getMethod() + " " + request.getRequestURI());

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length()).trim();

            Optional<String> subjectOpt = jwtProvider.extractSubject(token);
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            boolean notYetAuthenticated = existingAuth == null
                    || !existingAuth.isAuthenticated()
                    || (existingAuth instanceof AnonymousAuthenticationToken);
            if (subjectOpt.isPresent() && notYetAuthenticated) {
                String subject = subjectOpt.get();

                // Check if user is banned/suspended
                try {
                    Integer userId = Integer.parseInt(subject);
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent() && "SUSPENDED".equals(userOpt.get().getStatus())) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write(
                                "{\"error\":\"Account is suspended\",\"message\":\"Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.\"}");
                        return;
                    }
                } catch (NumberFormatException ignored) {
                    // If subject is not a number, skip user check
                }

                String role = jwtProvider.extractRole(token).orElse("USER");

                // Spring Security convention: ROLE_*
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                        role.startsWith("ROLE_") ? role : "ROLE_" + role);

                System.out.println("[JWT DEBUG] subject=" + subject + " role=" + role + " authority="
                        + authority.getAuthority() + " path=" + request.getRequestURI());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(subject,
                        null, List.of(authority));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }

        filterChain.doFilter(request, response);
    }
}