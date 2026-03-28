package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * CartItem entity — represents a single product entry in a user's cart.
 *
 * <p>Acts as a join entity between {@link Cart} and {@link Product},
 * adding a quantity field to track how many units the user wants.</p>
 *
 * <h3>Constraints:</h3>
 * <ul>
 *   <li>A product can appear only once per cart (enforced by unique constraint on cart_id + product_id)</li>
 *   <li>Quantity must be at least 1 (validated at the service layer)</li>
 *   <li>Quantity cannot exceed the product's available stock (validated at order placement)</li>
 * </ul>
 */
@Entity
@Table(name = "cart_items", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_cart_product",
        columnNames = {"cart_id", "product_id"}
    )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The cart this item belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    /** The product being added to the cart */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Number of units of this product in the cart. Must be >= 1. */
    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    /** When this item was added to the cart */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
