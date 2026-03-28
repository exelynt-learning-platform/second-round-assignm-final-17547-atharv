package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for a single item in the cart response.
 *
 * <p>Includes computed {@code subtotal} (price × quantity) so the frontend
 * doesn't need to perform this calculation.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal price;
    private Integer quantity;

    /** Computed field: price × quantity */
    private BigDecimal subtotal;

    /**
     * Converts a CartItem entity to a CartItemResponse DTO.
     * Eagerly fetches product details for the response.
     *
     * @param item the CartItem entity
     * @return a populated CartItemResponse with computed subtotal
     */
    public static CartItemResponse fromEntity(CartItem item) {
        BigDecimal price = item.getProduct().getPrice();
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImageUrl(item.getProduct().getImageUrl())
                .price(price)
                .quantity(item.getQuantity())
                .subtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}
