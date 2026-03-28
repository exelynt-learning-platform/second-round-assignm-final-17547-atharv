package com.ecommerce.backend.config;

import com.ecommerce.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration — defines authentication, authorization,
 * and filter chain setup for the entire application.
 *
 * <h3>Key decisions:</h3>
 * <ul>
 *   <li><b>Stateless sessions</b>: No server-side session storage — JWT handles state</li>
 *   <li><b>CSRF disabled</b>: Safe for stateless REST APIs (tokens prevent CSRF)</li>
 *   <li><b>BCrypt password encoding</b>: Industry-standard adaptive hashing</li>
 *   <li><b>JWT filter</b>: Runs before UsernamePasswordAuthenticationFilter</li>
 * </ul>
 *
 * <h3>URL access rules:</h3>
 * <ul>
 *   <li>{@code /api/auth/**} — public (registration, login)</li>
 *   <li>{@code GET /api/products/**} — public (browsing)</li>
 *   <li>{@code POST/PUT/DELETE /api/products/**} — ADMIN only</li>
 *   <li>{@code /api/cart/**, /api/orders/**} — authenticated users</li>
 *   <li>{@code /api/payments/webhook} — public (Stripe callbacks)</li>
 *   <li>Everything else — authenticated</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize, @Secured annotations on methods
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configures the security filter chain with URL-based access rules,
     * session management, and JWT filter placement.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — not needed for stateless JWT-based REST APIs.
            // CSRF protection is for browser-based session cookies; JWT tokens
            // are immune to CSRF attacks since they're sent in headers.
            .csrf(csrf -> csrf.disable())

            // Configure URL access rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints — no authentication required
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/payments/webhook").permitAll()
                .requestMatchers("/actuator/health").permitAll()

                // Product browsing is public; product management requires ADMIN role
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                // Admin-only endpoints for user management
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // All other endpoints require authentication
                .anyRequest().authenticated()
            )

            // Stateless session management — no HttpSession created or used.
            // Every request must carry its own authentication (JWT token).
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Set the custom authentication provider (uses our UserDetailsService + BCrypt)
            .authenticationProvider(authenticationProvider())

            // Add JWT filter BEFORE the default username/password filter.
            // This ensures JWT tokens are processed first.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Creates an AuthenticationProvider that uses our custom UserDetailsService
     * to load users and BCrypt to verify passwords.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Exposes the AuthenticationManager bean for use in the AuthService.
     * Required for programmatic authentication during login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt password encoder — industry-standard adaptive hashing.
     * Default strength is 10 rounds (2^10 = 1024 iterations).
     * Increase for higher security at the cost of CPU time.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}