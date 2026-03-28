package com.ecommerce.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration.
 *
 * <p>Allows the frontend application (running on a different port/domain)
 * to make API requests to this backend. Without this, browsers would
 * block cross-origin requests due to the Same-Origin Policy.</p>
 *
 * <h3>Production note:</h3>
 * <p>Update {@code allowedOrigins} to include only your actual frontend
 * domain(s) instead of using wildcards or localhost.</p>
 */
@Configuration
public class CorsConfig {

    /**
     * Configures CORS rules applied to all API endpoints.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origins allowed to make requests (update for production)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",     // React dev server
                "http://localhost:5173",     // Vite dev server
                "http://localhost:4200"      // Angular dev server
        ));

        // HTTP methods allowed
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Headers allowed in requests
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",    // JWT token
                "Content-Type",     // JSON content type
                "Accept",           // Accept header
                "Origin",           // Origin header
                "X-Requested-With"  // AJAX requests
        ));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // How long the browser can cache preflight response (1 hour)
        configuration.setMaxAge(3600L);

        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
