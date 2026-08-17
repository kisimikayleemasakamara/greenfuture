package com.ewomen.greenfuture.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "dGVzdC1vbmx5LWp3dC1zZWNyZXQtdGhhdC1pcy1sb25nLWVub3VnaA==";

    @Test
    void createsAValidShortLivedAccessToken() {
        Instant now = Instant.parse("2026-08-17T12:00:00Z");
        JwtService jwtService = new JwtService(
                TEST_SECRET,
                Duration.ofMinutes(15),
                Clock.fixed(now, ZoneOffset.UTC));

        String token = jwtService.generateToken("officer@example.org");

        assertThat(jwtService.extractEmail(token)).isEqualTo("officer@example.org");
        assertThat(jwtService.extractExpiration(token).toInstant())
                .isEqualTo(now.plus(Duration.ofMinutes(15)));
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void rejectsMalformedTokens() {
        JwtService jwtService = new JwtService(
                TEST_SECRET,
                Duration.ofMinutes(15),
                Clock.systemUTC());

        assertThat(jwtService.isTokenValid("not-a-jwt")).isFalse();
    }
}
