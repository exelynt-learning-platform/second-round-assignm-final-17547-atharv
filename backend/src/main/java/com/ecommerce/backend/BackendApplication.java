package com.ecommerce.backend;

import com.ecommerce.backend.constant.RoleConstants;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Main entry point for the E-Commerce Backend application.
 *
 * <p>Uses Spring Boot's auto-configuration to set up:</p>
 * <ul>
 *   <li>Embedded Tomcat server</li>
 *   <li>Spring Security with JWT authentication</li>
 *   <li>JPA/Hibernate with MySQL</li>
 *   <li>Bean Validation</li>
 *   <li>Stripe payment integration</li>
 *   <li>Actuator health endpoints</li>
 * </ul>
 */
@SpringBootApplication
public class BackendApplication {

    private static final Logger logger = LoggerFactory.getLogger(BackendApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    /**
     * Seeds a default admin user on application startup if one doesn't already exist.
     *
     * <p>This ensures there is always an admin account available for managing
     * products, viewing users, and updating order statuses.</p>
     *
     * <p><b>Production note:</b> Change the default admin password via environment
     * variables or remove this seeder and use a database migration script instead.</p>
     *
     * @param userRepository  repository for user persistence
     * @param passwordEncoder BCrypt encoder for hashing the admin password
     * @return CommandLineRunner that executes after application context is loaded
     */
    @Bean
    CommandLineRunner seedAdminUser(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@ecommerce.com";

            // Only create the admin if it doesn't already exist (idempotent)
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .firstName("Admin")
                        .lastName("User")
                        .email(adminEmail)
                        .password(passwordEncoder.encode("admin123"))
                        .role(RoleConstants.ADMIN)
                        .phone("0000000000")
                        .address("System Admin")
                        .build();

                userRepository.save(admin);
                logger.info("Default admin user created: {}", adminEmail);
            } else {
                logger.info("Admin user already exists: {}", adminEmail);
            }
        };
    }
}
