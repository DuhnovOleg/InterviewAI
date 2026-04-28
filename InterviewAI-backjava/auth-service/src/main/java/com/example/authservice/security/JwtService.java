package com.example.authservice.security;
import com.example.authservice.config.JwtProperties;
import com.example.authservice.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(Arrays.copyOf(keyBytes, 32));
    }

    public String generateAccessToken(UserEntity user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtProperties.getAccessTokenExpirationMinutes() * 60);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        claims.put("roles", user.getRoles().stream().map(Enum::name).toList());

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(user.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public AuthenticatedUserPrincipal toPrincipal(String token) {
        Claims claims = extractClaims(token);

        Object rolesObj = claims.get("roles");
        Set<String> roles = new HashSet<>();
        if (rolesObj instanceof Collection<?> collection) {
            roles = collection.stream().map(String::valueOf).collect(Collectors.toSet());
        }

        return AuthenticatedUserPrincipal.builder()
                .userId(UUID.fromString(claims.getSubject()))
                .email(String.valueOf(claims.get("email")))
                .username(String.valueOf(claims.get("username")))
                .roles(roles)
                .build();
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.getAccessTokenExpirationMinutes() * 60;
    }

}
