package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for placing an order from the user's cart.
 *
 * <p>The shipping address is captured at order time because users may
 * want to ship to a different address than their profile default.
 * All cart items are automatically transferred to the new order.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderRequest {

    /** Delivery address for this specific order */
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
}
