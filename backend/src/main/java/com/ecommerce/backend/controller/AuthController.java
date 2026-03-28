package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.LoginRequest;
import com.ecommerce.backend.dto.request.RegisterRequest;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.dto.response.AuthResponse;
import com.ecommerce.backend.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints — registration and login.
 *
 * <p>All endpoints under {@code /api/auth/**} are publically accessible
 * (configured in {@link com.ecommerce.backend.config.SecurityConfig}).</p>
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li>{@code POST /api/auth/register} — create a new user account</li>
 *   <li>{@code POST /api/auth/login} — authenticate and get JWT token</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account.
     *
     * <p>{@code @Valid} triggers Jakarta Bean Validation on the request body.
     * If validation fails, {@link com.ecommerce.backend.exception.GlobalExceptionHandler}
     * converts the errors to a 400 response automatically.</p>
     *
     * @param request validated registration data (firstName, lastName, email, password, etc.)
     * @return 201 Created with JWT token and user info
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request login credentials (email, password)
     * @return 200 OK with JWT token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }
}
