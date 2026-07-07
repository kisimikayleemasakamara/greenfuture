package com.ewomen.greenfuture.service;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY = "dGhpc0lzQVN1cGVyU2VjdXJlSldUU2VjcmV0S2V5Rm9yR3JlZW5GdXR1cmU=";

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
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
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.out.println("JWT Validation Error: " + e.getMessage());
            return false;
        }
    }
}

/**
 * @Service
 *          public class JwtService {
 * 
 *          private static final String SECRET_KEY =
 *          "dGhpc0lzQVN1cGVyU2VjdXJlSldUU2VjcmV0S2V5Rm9yR3JlZW5GdXR1cmU=";
 * 
 *          private Key getSigningKey() {
 *          byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
 *          return Keys.hmacShaKeyFor(keyBytes);
 *          }
 * 
 *          public String generateToken(String email) {
 * 
 *          return Jwts.builder()
 *          .setSubject(email)
 *          .setIssuedAt(new Date())
 *          .setExpiration(
 *          new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
 *          .signWith(getSigningKey(), SignatureAlgorithm.HS256)
 *          .compact();
 *          }
 * 
 *          public Claims extractEmail(String token) {
 *          return Jwts.parserBuilder()
 *          .setSigningKey(getSigningKey())
 *          .build()
 *          .parseClaimsJws(token)
 *          .getBody();
 *          }
 * 
 *          public boolean isTokenValid(String token) {
 *          try {
 *          return !isTokenExpired(token);
 *          } catch (Exception e) {
 *          return false;
 *          }
 *          }
 * 
 *          public boolean isTokenExpired(String token) {
 *          return extractExpiration(token).before(new Date());
 *          }
 * 
 *          public Date extractExpiration(String token) {
 *          return extractEmail(token).getExpiration();
 *          }
 *          }
 **/