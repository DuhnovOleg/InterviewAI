package com.example.authservice.service;
import com.example.authservice.config.JwtProperties;
import com.example.authservice.entity.RefreshTokenEntity;
import com.example.authservice.entity.UserEntity;
import com.example.authservice.exception.InvalidTokenException;
import com.example.authservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public String createRefreshToken(UserEntity user) {
        String rawToken = UUID.randomUUID() + "." + UUID.randomUUID();
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUserId(user.getId());
        entity.setTokenHash(hash(rawToken));
        entity.setExpiresAt(OffsetDateTime.now().plusDays(jwtProperties.getRefreshTokenExpirationDays()));
        entity.setRevoked(false);
        entity.setCreatedAt(OffsetDateTime.now());
        refreshTokenRepository.save(entity);
        return rawToken;
    }

    public UUID validateAndExtractUserId(String rawToken) {
        RefreshTokenEntity entity = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        if (entity.isRevoked()) {
            throw new InvalidTokenException("Refresh token revoked");
        }
        if (entity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new InvalidTokenException("Refresh token expired");
        }

        return entity.getUserId();
    }

    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken))
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot hash refresh token", e);
        }
    }

}