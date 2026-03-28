package com.ecommerce.backend.exception;

import com.ecommerce.backend.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler — intercepts all exceptions thrown by controllers
 * and converts them into consistent {@link ApiResponse} JSON responses.
 *
 * <p>This class uses {@code @RestControllerAdvice} to apply across ALL controllers.
 * Each {@code @ExceptionHandler} method handles a specific exception type and
 * returns the appropriate HTTP status code.</p>
 *
 * <h3>Exception → HTTP Status mapping:</h3>
 * <ul>
 *   <li>{@link ResourceNotFoundException} → 404 Not Found</li>
 *   <li>{@link DuplicateResourceException} → 409 Conflict</li>
 *   <li>{@link InsufficientStockException} → 400 Bad Request</li>
 *   <li>{@link BadCredentialsException} → 401 Unauthorized</li>
 *   <li>{@link AccessDeniedException} → 403 Forbidden</li>
 *   <li>{@link MethodArgumentNotValidException} → 400 Bad Request (validation errors)</li>
 *   <li>{@link PaymentException} → 502 Bad Gateway</li>
 *   <li>{@link Exception} → 500 Internal Server Error (catch-all)</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ======================== 404 NOT FOUND ========================

    /**
     * Handles cases where a requested resource (user, product, order, etc.) doesn't exist.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ======================== 409 CONFLICT ========================

    /**
     * Handles duplicate resource creation attempts (e.g., registering with an existing email).
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(DuplicateResourceException ex) {
        logger.warn("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ======================== 400 BAD REQUEST (Stock) ========================

    /**
     * Handles cases where the requested quantity exceeds available stock.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientStock(InsufficientStockException ex) {
        logger.warn("Insufficient stock: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ======================== 401 UNAUTHORIZED ========================

    /**
     * Handles failed authentication (wrong email/password).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid email or password"));
    }

    // ======================== 403 FORBIDDEN ========================

    /**
     * Handles attempts to access resources without sufficient permissions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access denied. You don't have permission to perform this action."));
    }

    // ======================== 400 BAD REQUEST (Validation) ========================

    /**
     * Handles Bean Validation failures (e.g., @NotBlank, @Email, @Min).
     * Collects all field errors into a map for detailed error reporting.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        // Collect all validation errors: field name → error message
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        logger.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }

    // ======================== 502 BAD GATEWAY (Payment) ========================

    /**
     * Handles payment gateway failures (Stripe errors, network issues, etc.).
     */
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentException(PaymentException ex) {
        logger.error("Payment processing failed: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.error("Payment processing failed: " + ex.getMessage()));
    }

    // ======================== 400 BAD REQUEST (Generic) ========================

    /**
     * Handles generic illegal argument exceptions from service layer validation.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ======================== 500 INTERNAL SERVER ERROR (Catch-All) ========================

    /**
     * Catch-all handler for unexpected exceptions. Logs the full stack trace
     * but returns a generic message to avoid leaking internal details.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
    }
}
