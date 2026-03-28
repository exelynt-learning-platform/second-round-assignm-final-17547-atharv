package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * OrderItem entity — represents a single line item within an order.
 *
 * <p>This entity captures a snapshot of the product's price at the time of
 * purchase ({@code priceAtPurchase}), ensuring that subsequent price changes
 * on the product do not alter historical order data.</p>
 *
 * <h3>Relationships:</h3>
 * <ul>
 *   <li>Many-to-One with {@link Order} — each item belongs to exactly one order</li>
 *   <li>Many-to-One with {@link Product} — references the purchased product</li>
 * </ul>
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The order this item belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** The product that was purchased */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Number of units purchased */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Price of a single unit at the time of purchase.
     * This is a historical snapshot — changes to Product.price do NOT affect this value.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtPurchase;
}
