package com.example.authservice.service;
import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.dto.respronse.AuthResponse;
import com.example.authservice.dto.respronse.MeResponse;
import com.example.authservice.dto.respronse.RegisterResponse;
import com.example.authservice.entity.UserEntity;
import com.example.authservice.enums.RoleType;
import com.example.authservice.exception.InvalidCredentialsException;
import com.example.authservice.exception.InvalidTokenException;
import com.example.authservice.exception.UserAlreadyExistsException;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.AuthenticatedUserPrincipal;
import com.example.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setUsername(request.getUsername().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.getRoles().add(RoleType.ROLE_USER);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        UserEntity saved = userRepository.save(user);

        return RegisterResponse.builder()
                .userId(saved.getId())
                .email(saved.getEmail())
                .username(saved.getUsername())
                .roles(saved.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .status("REGISTERED")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository
                .findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new InvalidCredentialsException("User disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return toAuthResponse(user, accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        UUID userId = refreshTokenService.validateAndExtractUserId(refreshToken);
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(() -> new InvalidTokenException("User for refresh token not found"));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.createRefreshToken(user);

        refreshTokenService.revoke(refreshToken);

        return toAuthResponse(user, newAccessToken, newRefreshToken);
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    public MeResponse me(AuthenticatedUserPrincipal principal) {
        return MeResponse
                .builder()
                .userId(principal.getUserId())
                .email(principal.getEmail())
                .username(principal.getUsername())
                .roles(principal.getRoles())
                .build();
    }

    private AuthResponse toAuthResponse(UserEntity user, String accessToken, String refreshToken) {
        return AuthResponse
                .builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresInSeconds(jwtService.getAccessTokenExpirationSeconds())
                .build();
    }

}
