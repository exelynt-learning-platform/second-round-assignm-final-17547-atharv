package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for order responses — includes order metadata and all line items.
 *
 * <p>Provides a complete view of an order suitable for both order
 * listing and order detail pages.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String stripeSessionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Converts an Order entity to an OrderResponse DTO.
     * Maps all order items and includes order-level metadata.
     *
     * @param order the Order entity with items loaded
     * @return a populated OrderResponse
     */
    public static OrderResponse fromEntity(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::fromEntity)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .items(itemResponses)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .stripeSessionId(order.getStripeSessionId())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
