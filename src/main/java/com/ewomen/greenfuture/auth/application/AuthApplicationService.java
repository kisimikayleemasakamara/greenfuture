package com.ewomen.greenfuture.auth.application;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ewomen.greenfuture.auth.infrastructure.persistence.RefreshSessionEntity;
import com.ewomen.greenfuture.auth.infrastructure.persistence.RefreshSessionRepository;
import com.ewomen.greenfuture.common.error.ApiException;
import com.ewomen.greenfuture.entity.User;
import com.ewomen.greenfuture.repository.UserRepository;
import com.ewomen.greenfuture.service.JwtService;

@Service
public class AuthApplicationService {

    private final UserRepository userRepository;
    private final RefreshSessionRepository refreshSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenCodec refreshTokenCodec;
    private final Duration refreshTokenTtl;

    public AuthApplicationService(
            UserRepository userRepository,
            RefreshSessionRepository refreshSessionRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenCodec refreshTokenCodec,
            @Value("${security.refresh-token.ttl:30d}") Duration refreshTokenTtl) {
        this.userRepository = userRepository;
        this.refreshSessionRepository = refreshSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenCodec = refreshTokenCodec;
        this.refreshTokenTtl = refreshTokenTtl;
    }

    @Transactional
    public IssuedSession login(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .filter(candidate -> passwordEncoder.matches(password, candidate.getPassword()))
                .orElseThrow(this::invalidCredentials);

        return issue(user, UUID.randomUUID(), Instant.now());
    }

    @Transactional(noRollbackFor = ApiException.class)
    public IssuedSession rotateRefreshSession(String rawRefreshToken) {
        Instant now = Instant.now();
        RefreshSessionEntity current = refreshSessionRepository
                .findByTokenHash(refreshTokenCodec.hash(rawRefreshToken))
                .orElseThrow(this::invalidRefreshSession);

        if (current.getRevokedAt() != null) {
            refreshSessionRepository.revokeActiveFamily(current.getFamilyId(), now);
            throw invalidRefreshSession();
        }

        if (!current.getExpiresAt().isAfter(now)) {
            current.revoke(now, null);
            throw invalidRefreshSession();
        }

        IssuedSession rotated = issue(current.getUser(), current.getFamilyId(), now);
        RefreshSessionEntity replacement = refreshSessionRepository
                .findByTokenHash(refreshTokenCodec.hash(rotated.refreshToken()))
                .orElseThrow();
        current.revoke(now, replacement.getId());
        return rotated;
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }

        refreshSessionRepository.findByTokenHash(refreshTokenCodec.hash(rawRefreshToken))
                .ifPresent(session -> refreshSessionRepository
                        .revokeActiveFamily(session.getFamilyId(), Instant.now()));
    }

    @Transactional(readOnly = true)
    public User currentUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.UNAUTHORIZED,
                        "AUTHENTICATION_REQUIRED",
                        "Authentication is required."));
    }

    private IssuedSession issue(User user, UUID familyId, Instant now) {
        String refreshToken = refreshTokenCodec.generate();
        RefreshSessionEntity session = new RefreshSessionEntity(
                UUID.randomUUID(),
                user,
                familyId,
                refreshTokenCodec.hash(refreshToken),
                now,
                now.plus(refreshTokenTtl));
        refreshSessionRepository.save(session);

        return new IssuedSession(user, jwtService.generateToken(user.getEmail()), refreshToken);
    }

    private ApiException invalidCredentials() {
        return new ApiException(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "The email or password is incorrect.");
    }

    private ApiException invalidRefreshSession() {
        return new ApiException(
                HttpStatus.UNAUTHORIZED,
                "INVALID_REFRESH_SESSION",
                "The refresh session is invalid or expired.");
    }
}
