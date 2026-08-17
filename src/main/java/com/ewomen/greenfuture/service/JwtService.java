package com.ewomen.greenfuture.service;

import java.security.Key;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final Key signingKey;
    private final Duration accessTokenTtl;
    private final Clock clock;

    @Autowired
    public JwtService(
            @Value("${security.jwt.secret-base64}") String secretBase64,
            @Value("${security.jwt.access-token-ttl:15m}") Duration accessTokenTtl) {
        this(secretBase64, accessTokenTtl, Clock.systemUTC());
    }

    JwtService(String secretBase64, Duration accessTokenTtl, Clock clock) {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenTtl = accessTokenTtl;
        this.clock = clock;
    }

    private Key getSigningKey() {
        return signingKey;
    }

    public String generateToken(String email) {

        Instant issuedAt = clock.instant();

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(issuedAt.plus(accessTokenTtl)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).toInstant().isBefore(clock.instant());
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
