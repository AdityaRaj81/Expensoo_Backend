package com.expenso.expense_tracker.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.expenso.expense_tracker.enums.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT Service
 *
 * Responsible for:
 * - Generating JWT Tokens
 * - Validating JWT Tokens
 * - Extracting JWT Claims
 * - Extracting User Information
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey secretKey;

    /**
     * Initialize Secret Key
     */
    @PostConstruct
    public void initialize() {

        this.secretKey = Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );

    }

    /**
     * Generate JWT Token
     */
    public String generateToken(
            UUID userId,
            String email,
            UserRole role
    ) {

        Date issuedAt = new Date();

        Date expiration = new Date(
                issuedAt.getTime() + jwtExpiration
        );

        return Jwts.builder()

                .subject(email)

                .claim(
                        "userId",
                        userId.toString()
                )

                .claim(
                        "role",
                        role.name()
                )

                .issuedAt(issuedAt)

                .expiration(expiration)

                .signWith(secretKey)

                .compact();

    }

    /**
     * Remove Bearer Prefix
     */
    private String extractRawToken(String token) {

        if (token == null || token.isBlank()) {

            throw new RuntimeException("JWT token is missing.");

        }

        if (token.startsWith(SecurityConstants.TOKEN_PREFIX)) {

            return token.substring(
                    SecurityConstants.TOKEN_PREFIX.length()
            );

        }

        return token;

    }

    /**
     * Parse JWT Claims
     */
    private Claims getClaims(String token) {

        try {

            return Jwts.parser()

                    .verifyWith(secretKey)

                    .build()

                    .parseSignedClaims(
                            extractRawToken(token)
                    )

                    .getPayload();

        } catch (JwtException exception) {

            throw new RuntimeException(
                    "Invalid or expired JWT token."
            );

        }

    }

    /**
     * Extract User ID
     */
    public UUID extractUserId(String token) {

        String userId = getClaims(token)

                .get(
                        "userId",
                        String.class
                );

        return UUID.fromString(userId);

    }

    /**
     * Extract User Email
     */
    public String extractUsername(String token) {

        return getClaims(token)

                .getSubject();

    }

    /**
     * Extract User Role
     */
    public UserRole extractRole(String token) {

        String role = getClaims(token)

                .get(
                        "role",
                        String.class
                );

        return UserRole.valueOf(role);

    }

    /**
     * Validate JWT Token
     */
    public boolean isTokenValid(String token) {

        try {

            getClaims(token);

            return true;

        } catch (Exception exception) {

            return false;

        }

    }

    /**
     * Check Token Expiration
     */
    public boolean isTokenExpired(String token) {

        Date expiration = getClaims(token)

                .getExpiration();

        return expiration.before(
                new Date()
        );

    }

}