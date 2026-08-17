package com.ewomen.greenfuture.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ewomen.greenfuture.auth.infrastructure.persistence.RefreshSessionEntity;
import com.ewomen.greenfuture.auth.infrastructure.persistence.RefreshSessionRepository;
import com.ewomen.greenfuture.common.error.ApiException;
import com.ewomen.greenfuture.entity.User;
import com.ewomen.greenfuture.repository.UserRepository;
import com.ewomen.greenfuture.service.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshSessionRepository refreshSessionRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenCodec refreshTokenCodec;

    private AuthApplicationService service;

    @BeforeEach
    void setUp() {
        service = new AuthApplicationService(
                userRepository,
                refreshSessionRepository,
                passwordEncoder,
                jwtService,
                refreshTokenCodec,
                Duration.ofDays(30));
    }

    @Test
    void loginNormalizesEmailAndStoresOnlyTheRefreshTokenHash() {
        User user = user("officer@example.org");
        when(userRepository.findByEmailIgnoreCase("officer@example.org")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correct password", user.getPassword())).thenReturn(true);
        when(refreshTokenCodec.generate()).thenReturn("opaque-refresh-token");
        when(refreshTokenCodec.hash("opaque-refresh-token")).thenReturn("stored-hash");
        when(jwtService.generateToken(user.getEmail())).thenReturn("access-token");

        IssuedSession issued = service.login(" Officer@Example.org ", "correct password");

        assertThat(issued.accessToken()).isEqualTo("access-token");
        assertThat(issued.refreshToken()).isEqualTo("opaque-refresh-token");
        verify(userRepository).findByEmailIgnoreCase("officer@example.org");
        verify(refreshSessionRepository).save(any(RefreshSessionEntity.class));
    }

    @Test
    void reuseOfARevokedTokenRevokesTheRemainingFamily() {
        UUID familyId = UUID.randomUUID();
        RefreshSessionEntity reused = new RefreshSessionEntity(
                UUID.randomUUID(),
                user("officer@example.org"),
                familyId,
                "old-hash",
                Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(3600));
        reused.revoke(Instant.now().minusSeconds(30), UUID.randomUUID());
        when(refreshTokenCodec.hash("reused-token")).thenReturn("old-hash");
        when(refreshSessionRepository.findByTokenHash("old-hash"))
                .thenReturn(Optional.of(reused));

        assertThatThrownBy(() -> service.rotateRefreshSession("reused-token"))
                .isInstanceOf(ApiException.class)
                .hasMessage("The refresh session is invalid or expired.");

        verify(refreshSessionRepository).revokeActiveFamily(any(UUID.class), any(Instant.class));
    }

    @Test
    void rotationRevokesTheOldSessionAndReturnsANewToken() {
        UUID familyId = UUID.randomUUID();
        User user = user("officer@example.org");
        RefreshSessionEntity current = new RefreshSessionEntity(
                UUID.randomUUID(), user, familyId, "old-hash",
                Instant.now().minusSeconds(60), Instant.now().plusSeconds(3600));
        RefreshSessionEntity replacement = new RefreshSessionEntity(
                UUID.randomUUID(), user, familyId, "new-hash",
                Instant.now(), Instant.now().plusSeconds(3600));

        when(refreshTokenCodec.hash("old-token")).thenReturn("old-hash");
        when(refreshTokenCodec.generate()).thenReturn("new-token");
        when(refreshTokenCodec.hash("new-token")).thenReturn("new-hash");
        when(refreshSessionRepository.findByTokenHash("old-hash"))
                .thenReturn(Optional.of(current));
        when(refreshSessionRepository.findByTokenHash("new-hash"))
                .thenReturn(Optional.of(replacement));
        when(jwtService.generateToken(user.getEmail())).thenReturn("new-access-token");

        IssuedSession rotated = service.rotateRefreshSession("old-token");

        assertThat(rotated.refreshToken()).isEqualTo("new-token");
        assertThat(current.getRevokedAt()).isNotNull();
        ArgumentCaptor<RefreshSessionEntity> saved = ArgumentCaptor.forClass(RefreshSessionEntity.class);
        verify(refreshSessionRepository).save(saved.capture());
        assertThat(saved.getValue().getFamilyId()).isEqualTo(familyId);
    }

    private User user(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded-password");
        return user;
    }
}
