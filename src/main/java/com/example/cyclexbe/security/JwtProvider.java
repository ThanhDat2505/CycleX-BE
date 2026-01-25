package com.example.cyclexbe.security;

import com.example.cyclexbe.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long expirationSeconds;
    private final String issuer;

    public JwtProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds:86400}") long expirationSeconds,
            @Value("${security.jwt.issuer:cyclex}") String issuer
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(ensureBase64(secret)));
        this.expirationSeconds = expirationSeconds;
        this.issuer = issuer;
    }

    /**
     * Tạo JWT với subject = email/username, role là String (ví dụ: "ADMIN" hoặc "USER").
     */
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getUserId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("role", user.getRole())
                .claim("email", user.getCccd())
                .signWith(secretKey)
                .compact();
    }

    public Optional<Jws<Claims>> parseAndValidate(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token);
            return Optional.of(jws);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public Optional<String> extractSubject(String token) {
        return parseAndValidate(token).map(jws -> jws.getPayload().getSubject());
    }

    public Optional<String> extractRole(String token) {
        return parseAndValidate(token).map(jws -> {
            Object role = jws.getPayload().get("role");
            return role == null ? null : role.toString();
        });
    }

    /**
     * Bạn có 2 lựa chọn:
     * 1) Lưu secret dạng BASE64 (khuyến nghị) -> ok
     * 2) Lưu secret dạng plain text -> convert tạm thời (để khỏi crash lúc dev)
     */
    private static String ensureBase64(String secretOrBase64) {
        // Nếu người dùng nhập plain text, encode tạm sang Base64 để JJWT decode được.
        // Khi production: hãy dùng BASE64 thật (chuỗi đủ dài).
        try {
            Decoders.BASE64.decode(secretOrBase64);
            return secretOrBase64;
        } catch (Exception ignored) {
            return java.util.Base64.getEncoder().encodeToString(secretOrBase64.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
    }
}