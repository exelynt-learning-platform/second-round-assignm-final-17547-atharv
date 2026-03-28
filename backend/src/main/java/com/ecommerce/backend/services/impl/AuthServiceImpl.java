package com.ecommerce.backend.services.impl;

import com.ecommerce.backend.dto.request.LoginRequest;
import com.ecommerce.backend.dto.request.RegisterRequest;
import com.ecommerce.backend.dto.response.AuthResponse;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.DuplicateResourceException;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.services.AuthService;
import com.ecommerce.backend.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link AuthService} — handles user registration and login.
 *
 * <h3>Registration flow:</h3>
 * <ol>
 *   <li>Check if email is already registered → throw 409 if yes</li>
 *   <li>Build User entity with BCrypt-hashed password</li>
 *   <li>Save user to database</li>
 *   <li>Generate JWT token</li>
 *   <li>Return AuthResponse with token and user info</li>
 * </ol>
 *
 * <h3>Login flow:</h3>
 * <ol>
 *   <li>Authenticate via Spring Security's AuthenticationManager</li>
 *   <li>If credentials are valid, generate JWT token</li>
 *   <li>Return AuthResponse with token and user info</li>
 * </ol>
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Step 1: Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Step 2: Build and save the new user entity
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // BCrypt hash
                .phone(request.getPhone())
                .address(request.getAddress())
                .role("ROLE_USER")  // Default role for new registrations
                .build();

        User savedUser = userRepository.save(user);
        logger.info("New user registered: {}", savedUser.getEmail());

        // Step 3: Generate JWT token for immediate login
        String token = jwtUtil.generateToken(savedUser);

        // Step 4: Build and return auth response
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFirstName() + " " + savedUser.getLastName())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Step 1: Authenticate using Spring Security's AuthenticationManager
        // This internally uses our CustomUserDetailsService + PasswordEncoder
        // Throws BadCredentialsException if authentication fails
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Step 2: Get the authenticated user details
        User user = (User) authentication.getPrincipal();
        logger.info("User logged in: {}", user.getEmail());

        // Step 3: Generate JWT token
        String token = jwtUtil.generateToken(user);

        // Step 4: Build and return auth response
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole())
                .build();
    }
}
