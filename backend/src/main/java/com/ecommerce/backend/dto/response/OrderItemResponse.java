package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for a single line item within an order response.
 *
 * <p>Shows the product details and the price at the time of purchase,
 * so users can review their historical orders accurately.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long productId;
    private String productName;
    private Integer quantity;

    /** The price per unit when the order was placed (historical snapshot) */
    private BigDecimal priceAtPurchase;

    /** Computed: priceAtPurchase × quantity */
    private BigDecimal subtotal;

    /**
     * Converts an OrderItem entity to an OrderItemResponse DTO.
     *
     * @param item the OrderItem entity
     * @return a populated OrderItemResponse with computed subtotal
     */
    public static OrderItemResponse fromEntity(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .subtotal(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}
