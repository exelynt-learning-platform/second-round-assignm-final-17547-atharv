package com.ecommerce.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found in the database.
 *
 * <p>Automatically mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 * Use this instead of returning null or Optional.empty() from service methods.</p>
 *
 * <h3>Example usage:</h3>
 * <pre>{@code
 * Product product = productRepository.findById(id)
 *     .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
 * // → "Product not found with id: 42"
 * }</pre>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a formatted "not found" message.
     *
     * @param resourceName the type of resource (e.g., "Product", "User")
     * @param fieldName    the field used for lookup (e.g., "id", "email")
     * @param fieldValue   the value that was searched for
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
