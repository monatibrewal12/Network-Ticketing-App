package com.networkticketingapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Key SECRET_KEY =
            Keys.hmacShaKeyFor(
                    "network-ticketing-secret-key-1234567890"
                            .getBytes()
            );

    private static final long EXPIRATION_TIME = 86400000; // 1 day

    // 🔐 GENERATE TOKEN WITH USER DETAILS
    public String generateToken(Long userId, String username, String role) {
        return Jwts.builder()
                .setSubject(username)          // email / username
                .claim("userId", userId)       // 👈 IMPORTANT
                .claim("role", role)           // 👈 IMPORTANT
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRATION_TIME)
                )
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ BACKWARD COMPATIBLE (if used anywhere)
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRATION_TIME)
                )
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔍 EXTRACT USERNAME (EMAIL)
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 🔍 EXTRACT USER ID
    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get("userId");
        return userId != null ? Long.parseLong(userId.toString()) : null;
    }

    // 🔍 EXTRACT ROLE
    public String extractRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    // 🔍 COMMON CLAIM EXTRACTION
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ⏱ TOKEN VALIDATION
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }
}
