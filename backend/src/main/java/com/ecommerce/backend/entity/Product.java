package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product entity — represents an item available for purchase in the store.
 *
 * <p>Uses {@link BigDecimal} for price to avoid floating-point rounding errors
 * that are unacceptable in financial calculations.</p>
 *
 * <h3>Key design decisions:</h3>
 * <ul>
 *   <li>Price precision: 10 digits total, 2 decimal places (max 99,999,999.99)</li>
 *   <li>Stock quantity is validated at the service layer during cart/order operations</li>
 *   <li>Category is stored as a simple string for flexibility (can be upgraded to an enum or separate entity later)</li>
 * </ul>
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_category", columnList = "category"),
    @Index(name = "idx_product_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Product display name — required, max 200 characters */
    @Column(nullable = false, length = 200)
    private String name;

    /** Detailed product description shown on the product page */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Product price in the store's base currency.
     * Uses BigDecimal for exact decimal arithmetic (no floating-point errors).
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Number of units available in inventory. Must be >= 0. */
    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    /** URL pointing to the product image (can be CDN URL or relative path) */
    @Column(length = 500)
    private String imageUrl;

    /** Product category for filtering and browsing (e.g., "Electronics", "Clothing") */
    @Column(length = 100)
    private String category;

    /** Timestamp when the product was added to the catalog */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of the last product information update */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
