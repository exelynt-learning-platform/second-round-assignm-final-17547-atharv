package com.ecommerce.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a cart or order operation requires more stock
 * than is currently available for a product.
 *
 * <p>Mapped to HTTP 400 Bad Request. Includes the product name
 * and available stock in the error message to help the user.</p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientStockException extends RuntimeException {

    /**
     * @param productName the name of the out-of-stock product
     * @param available   the number of units currently in stock
     */
    public InsufficientStockException(String productName, int available) {
        super(String.format("Insufficient stock for '%s'. Only %d units available.", productName, available));
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}
