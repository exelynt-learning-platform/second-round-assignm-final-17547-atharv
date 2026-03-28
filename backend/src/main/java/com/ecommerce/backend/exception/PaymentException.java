package com.ecommerce.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a payment processing operation fails.
 *
 * <p>Mapped to HTTP 502 Bad Gateway since the failure originates
 * from the external payment provider (Stripe), not our application.</p>
 */
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class PaymentException extends RuntimeException {

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
