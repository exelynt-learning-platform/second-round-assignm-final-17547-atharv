package com.ecommerce.backend.security;

import com.ecommerce.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter — intercepts every HTTP request to validate
 * the JWT token and set up Spring Security's authentication context.
 *
 * <p>Extends {@link OncePerRequestFilter} to guarantee single execution
 * per request even in complex filter chains.</p>
 *
 * <h3>Filter flow:</h3>
 * <ol>
 *   <li>Extract the {@code Authorization} header from the request</li>
 *   <li>Check if it starts with "Bearer " — skip if not</li>
 *   <li>Extract the JWT token and parse the username (email) from it</li>
 *   <li>If no authentication exists yet, load UserDetails from the database</li>
 *   <li>Validate the token against the user details</li>
 *   <li>If valid, set the authentication in SecurityContext</li>
 *   <li>Continue the filter chain</li>
 * </ol>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Extract the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // Step 2: Check if the header contains a Bearer token
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            // No JWT token present — let the request continue without authentication
            // (public endpoints will pass; protected endpoints will be blocked by SecurityConfig)
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Step 3: Extract the token (remove "Bearer " prefix)
            final String jwt = authHeader.substring(BEARER_PREFIX.length());

            // Step 4: Extract the username (email) from the token
            final String userEmail = jwtUtil.extractUsername(jwt);

            // Step 5: Only process if we have a username and no existing authentication
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Step 6: Load user details from the database
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Step 7: Validate the token against user details
                if (jwtUtil.isTokenValid(jwt, userDetails)) {

                    // Step 8: Create authentication token with user details and authorities
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,        // Principal
                                    null,               // Credentials (not needed after auth)
                                    userDetails.getAuthorities()  // Granted authorities (roles)
                            );

                    // Attach request details (IP address, session ID, etc.)
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Step 9: Set the authentication in SecurityContext
                    // This makes the authenticated user available throughout the request
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.debug("Authenticated user: {} with role: {}",
                            userEmail, userDetails.getAuthorities());
                }
            }
        } catch (Exception e) {
            // Token is invalid/expired/malformed — log and continue without authentication
            logger.warn("JWT authentication failed: {}", e.getMessage());
        }

        // Continue the filter chain regardless of authentication outcome
        filterChain.doFilter(request, response);
    }
}
