package com.example.cyclexbe.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        // public endpoints - Auth
                        .requestMatchers("/api/auth/**").permitAll()

                        // public endpoints - BikeListings (Read-only public, write requires SELLER)
                        .requestMatchers(HttpMethod.GET, "/api/bikelistings", "/api/bikelistings/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bikelistings").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PUT, "/api/bikelistings/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/api/bikelistings/**").hasRole("SELLER")

                        // Inspector
                        .requestMatchers("/api/inspector/**").hasAnyRole("INSPECTOR", "ADMIN")

                        // Disputes - public reasons endpoint, authenticated for the rest
                        .requestMatchers(HttpMethod.GET, "/api/disputes/reasons").permitAll()
                        .requestMatchers("/api/disputes/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/buyers/*/dispute-eligibility").hasRole("BUYER")

                        // Authenticated endpoints - Seller (Batch 1)
                        .requestMatchers(HttpMethod.GET, "/api/seller/*/dashboard/stats").hasRole("SELLER")
                        .requestMatchers(HttpMethod.GET, "/api/seller/*/listings/search").hasRole("SELLER")
                        .requestMatchers(HttpMethod.POST, "/api/seller/listings/detail").hasRole("SELLER")
                        .requestMatchers(HttpMethod.POST, "/api/seller/listings/rejection").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PATCH, "/api/seller/listings/**").hasRole("SELLER")

                        // Authenticated endpoints - Seller (Future batches)
                        .requestMatchers("/api/seller/{sellerId}/**").hasRole("SELLER")

                        // Authenticated endpoints - Inspection Chat (S-40)
                        .requestMatchers("/api/inspection-requests/**").hasAnyRole("SELLER", "INSPECTOR", "ADMIN")

                        // Authenticated endpoints - Purchase Request (S-50)
                        .requestMatchers(HttpMethod.GET, "/api/products/*/purchase-requests/init").hasRole("BUYER")
                        .requestMatchers(HttpMethod.POST, "/api/products/*/purchase-requests/review").hasRole("BUYER")
                        .requestMatchers(HttpMethod.POST, "/api/products/*/purchase-requests").hasRole("BUYER")

                        // Authenticated endpoints - Seller Transactions (S-52)
                        .requestMatchers(HttpMethod.GET, "/api/seller/transactions/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.POST, "/api/seller/transactions/**").hasRole("SELLER")
                        // S54
                        .requestMatchers(HttpMethod.GET, "/api/buyer/transactions/**").hasRole("BUYER")
                        .requestMatchers(HttpMethod.POST, "/api/buyer/transactions/**").hasRole("BUYER")

                        // Authenticated endpoints - Shipper Dashboard (S-60) & Delivery (S-63)
                        .requestMatchers(HttpMethod.GET, "/api/shipper/**").hasRole("SHIPPER")
                        .requestMatchers(HttpMethod.POST, "/api/shipper/**").hasRole("SHIPPER")
                        .requestMatchers(HttpMethod.PUT, "/api/shipper/**").hasRole("SHIPPER")
                        .requestMatchers(HttpMethod.PATCH, "/api/shipper/**").hasRole("SHIPPER")

                        // Authenticated endpoints - Notifications (all roles)
                        .requestMatchers("/api/notifications/**").authenticated()

                        // Authenticated endpoints - Orders
                        .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("BUYER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/buyer").hasRole("BUYER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/seller").hasRole("SELLER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").authenticated()

                        // Authenticated endpoints - Notifications (all roles)
                        .requestMatchers("/api/notifications/**").authenticated()

                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}