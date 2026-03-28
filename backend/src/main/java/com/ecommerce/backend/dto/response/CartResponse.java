package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for the complete cart response.
 *
 * <p>Includes all cart items and a computed {@code totalPrice} summing
 * all item subtotals. This gives the frontend a complete cart summary
 * in a single API call.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long id;

    /** Individual items in the cart with product details and subtotals */
    private List<CartItemResponse> items;

    /** Total price of all items in the cart (sum of all subtotals) */
    private BigDecimal totalPrice;

    /** Number of distinct products in the cart */
    private int itemCount;

    /**
     * Converts a Cart entity to a CartResponse DTO.
     * Maps all items and computes the total price.
     *
     * @param cart the Cart entity with items loaded
     * @return a populated CartResponse with computed totals
     */
    public static CartResponse fromEntity(Cart cart) {
        // Convert each CartItem entity to a CartItemResponse DTO
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(CartItemResponse::fromEntity)
                .collect(Collectors.toList());

        // Sum up all item subtotals for the grand total
        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalPrice(totalPrice)
                .itemCount(itemResponses.size())
                .build();
    }
}
