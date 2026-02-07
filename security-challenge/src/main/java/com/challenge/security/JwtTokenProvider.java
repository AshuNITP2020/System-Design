package com.challenge.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Token Provider.
 * Handles generation, validation, and extraction of information from JWT tokens.
 * 
 * JWT Structure: header.payload.signature
 * - Header and Payload are Base64URL ENCODED (not encrypted - anyone can decode)
 * - Signature is ENCRYPTED using HMAC with secret key (prevents tampering)
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // in milliseconds

    /**
     * Get the signing key from the secret.
     * This key is used to encrypt/decrypt the JWT signature.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate a JWT token for a user.
     * 
     * @param userDetails the user details to include in the token
     * @return JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // Add custom claims to the token payload
        // These will be Base64URL encoded (not encrypted - anyone can decode!)
        if (userDetails instanceof com.challenge.entity.User) {
            com.challenge.entity.User user = (com.challenge.entity.User) userDetails;
            claims.put("userId", user.getId());
            claims.put("role", user.getRole().name());
        }
        
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Create a JWT token with claims and subject.
     * 
     * @param claims custom claims to include in the token
     * @param subject the username (subject of the token)
     * @return JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
            .claims(claims)  // Custom claims (userId, role, etc.)
            .subject(subject)  // Username
            .issuedAt(now)  // Token creation time
            .expiration(expiryDate)  // Token expiration time
            .signWith(getSigningKey())  // ENCRYPT signature with secret key
            .compact();  // Build and return the token string
    }

    /**
     * Extract username from JWT token.
     * 
     * @param token JWT token string
     * @return username (subject)
     */
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from JWT token.
     * 
     * @param token JWT token string
     * @return user ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extract role from JWT token.
     * 
     * @param token JWT token string
     * @return role name
     */
    public String getRoleFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Extract expiration date from JWT token.
     * 
     * @param token JWT token string
     * @return expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from the token.
     * 
     * @param token JWT token string
     * @param claimsResolver function to extract the claim
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from the token.
     * This method also validates the token signature.
     * 
     * @param token JWT token string
     * @return all claims from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())  // Verify signature using secret key
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Check if token is expired.
     * 
     * @param token JWT token string
     * @return true if token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Validate JWT token.
     * Checks if token is valid (not expired and signature is correct).
     * 
     * @param token JWT token string
     * @param userDetails user details to validate against
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Validate JWT token (without user details).
     * Only checks if token is not expired and signature is valid.
     * 
     * @param token JWT token string
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token) {
        try {
            // This will throw an exception if token is invalid or expired
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}

