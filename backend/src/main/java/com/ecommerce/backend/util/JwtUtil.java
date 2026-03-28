package com.ecommerce.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT utility class — handles token creation, parsing, and validation.
 *
 * <p>Uses the JJWT library (v0.12.x) for secure JWT operations with
 * HMAC-SHA256 signing. The secret key is loaded from application configuration
 * and must be Base64-encoded (minimum 256 bits for HS256).</p>
 *
 * <h3>Token structure:</h3>
 * <ul>
 *   <li><b>Subject</b>: user's email address (used as username)</li>
 *   <li><b>Claims</b>: includes "role" for authorization</li>
 *   <li><b>Issued At</b>: token creation timestamp</li>
 *   <li><b>Expiration</b>: configurable via {@code jwt.expiration} (default 24h)</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * <pre>{@code
 * // Generate token during login:
 * String token = jwtUtil.generateToken(userDetails);
 *
 * // Validate token in filter:
 * if (jwtUtil.isTokenValid(token, userDetails)) { ... }
 * }</pre>
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    /** Base64-encoded secret key for HMAC-SHA256 signing */
    @Value("${jwt.secret}")
    private String secret;

    /** Token validity duration in milliseconds */
    @Value("${jwt.expiration}")
    private long expiration;

    // ======================== TOKEN GENERATION ========================

    /**
     * Generates a JWT token for the given user.
     * Includes the user's role as a custom claim for authorization.
     *
     * @param userDetails the authenticated user's details
     * @return signed JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Add role to token claims so we can extract it without a DB query
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates and signs a JWT with the given claims and subject.
     *
     * @param claims  custom claims to include in the token payload
     * @param subject the token subject (user's email)
     * @return signed JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)                                          // Custom claims (role, etc.)
                .subject(subject)                                        // User identifier (email)
                .issuedAt(new Date(System.currentTimeMillis()))           // Token creation time
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Token expiry
                .signWith(getSignKey())                                   // Sign with HMAC-SHA key
                .compact();                                              // Build the token string
    }

    // ======================== TOKEN PARSING ========================

    /**
     * Extracts the username (email) from a JWT token.
     *
     * @param token the JWT token string
     * @return the email stored in the token's subject claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the user's role from a JWT token.
     *
     * @param token the JWT token string
     * @return the role string (e.g., "ROLE_USER")
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token the JWT token string
     * @return the token's expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from a JWT token using a resolver function.
     *
     * @param token          the JWT token string
     * @param claimsResolver function that extracts the desired claim from the Claims object
     * @param <T>            the type of the claim value
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT and extracts all claims.
     * Throws an exception if the token is invalid or expired.
     *
     * @param token the JWT token string
     * @return all claims contained in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())   // Verify signature
                .build()
                .parseSignedClaims(token)   // Parse and validate
                .getPayload();              // Extract claims
    }

    // ======================== TOKEN VALIDATION ========================

    /**
     * Validates a JWT token against the given user details.
     * A token is valid if:
     * 1. The username in the token matches the UserDetails username
     * 2. The token has not expired
     *
     * @param token       the JWT token to validate
     * @param userDetails the user details to validate against
     * @return true if the token is valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token the JWT token to check
     * @return true if the token's expiration date is before the current time
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ======================== KEY MANAGEMENT ========================

    /**
     * Decodes the Base64-encoded secret and creates an HMAC-SHA key.
     * The key must be at least 256 bits (32 bytes) for HS256 algorithm security.
     *
     * @return the SecretKey for signing and verifying tokens
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}