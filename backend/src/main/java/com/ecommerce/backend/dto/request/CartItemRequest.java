package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding a product to the user's cart.
 *
 * <p>The product ID identifies which product to add, and quantity
 * specifies how many units. If the product is already in the cart,
 * the service layer will update the existing quantity.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequest {

    /** ID of the product to add to the cart */
    @NotNull(message = "Product ID is required")
    private Long productId;

    /** Number of units to add — must be at least 1 */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
