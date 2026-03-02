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
                        // public endpoints - Auth
                        .requestMatchers("/api/auth/**").permitAll()

                        // public endpoints - BikeListings (Read)
                        .requestMatchers(HttpMethod.GET, "/api/bikelistings", "/api/bikelistings/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bikelistings").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/bikelistings/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/bikelistings/**").permitAll()

                        //Inspector
                        .requestMatchers("/api/inspector/**").permitAll()

                        // Authenticated endpoints - Seller (Batch 1)
                        .requestMatchers(HttpMethod.GET, "/api/seller/dashboard/stats").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/seller/*/listings/search").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/seller/listings/detail").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/seller/listings/rejection").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/seller/listings/**").permitAll()

                        // Authenticated endpoints - Seller (Future batches)
                        .requestMatchers("/api/seller/{sellerId}/**").permitAll()

                        // Authenticated endpoints - Inspection Chat (S-40)
                        .requestMatchers("/api/inspection-requests/**").hasAnyRole("SELLER", "INSPECTOR", "ADMIN")

                        // Authenticated endpoints - Purchase Request (S-50)
                        .requestMatchers(HttpMethod.GET, "/api/products/*/purchase-request/summary").hasRole("BUYER")
                        .requestMatchers(HttpMethod.POST, "/api/products/*/purchase-request").hasRole("BUYER")

                        // Authenticated endpoints - Seller Transactions (S-52)
                        .requestMatchers(HttpMethod.GET, "/api/seller/transactions/**").hasRole("SELLER")
                        //S54
                        .requestMatchers(HttpMethod.GET, "/api/buyer/transactions/**").hasRole("BUYER")

                        // Authenticated endpoints - Shipper Dashboard (S-60)
                        .requestMatchers(HttpMethod.GET, "/api/shipper/**").hasRole("SHIPPER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}