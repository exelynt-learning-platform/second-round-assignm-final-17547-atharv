package com.ecommerce.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to create a resource that already exists.
 *
 * <p>Mapped to HTTP 409 Conflict. Common use case: registering
 * with an email that's already taken.</p>
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
