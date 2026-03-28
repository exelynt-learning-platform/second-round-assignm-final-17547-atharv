package com.ecommerce.backend.services;

import com.ecommerce.backend.dto.request.LoginRequest;
import com.ecommerce.backend.dto.request.RegisterRequest;
import com.ecommerce.backend.dto.response.AuthResponse;

/**
 * Service interface for authentication operations.
 *
 * <p>Handles user registration and login, including password hashing,
 * credential validation, and JWT token generation.</p>
 */
public interface AuthService {

    /**
     * Registers a new user account.
     * Validates that the email is unique, hashes the password,
     * and returns a JWT token for immediate login.
     *
     * @param request the registration details
     * @return AuthResponse containing JWT token and user info
     * @throws com.ecommerce.backend.exception.DuplicateResourceException if email already exists
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user with email and password.
     * Validates credentials and returns a JWT token.
     *
     * @param request the login credentials
     * @return AuthResponse containing JWT token and user info
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     */
    AuthResponse login(LoginRequest request);
}
