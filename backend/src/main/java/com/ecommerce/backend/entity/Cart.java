package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart entity — represents a user's shopping cart.
 *
 * <p>Each user has exactly one cart (One-to-One relationship).
 * The cart contains multiple {@link CartItem} entries, each linking
 * to a specific product with a quantity.</p>
 *
 * <h3>Design notes:</h3>
 * <ul>
 *   <li>Cart is automatically created when a user adds their first item</li>
 *   <li>Cart persists across sessions — items are not lost on logout</li>
 *   <li>When an order is placed, cart items are moved to order items and the cart is cleared</li>
 * </ul>
 */
@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who owns this cart. One-to-One relationship.
     * JoinColumn creates a foreign key column 'user_id' in the carts table.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Items currently in the cart. CascadeType.ALL ensures that when
     * we save/update/delete the cart, all its items are handled too.
     * orphanRemoval = true deletes CartItem records when removed from this list.
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    /** When this cart was first created */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** When the cart was last modified (item added/removed/quantity changed) */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
