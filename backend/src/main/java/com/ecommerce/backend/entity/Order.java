package com.ecommerce.backend.entity;

import com.ecommerce.backend.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity — represents a completed purchase by a user.
 *
 * <p>Orders are created from the user's cart. Each order captures a snapshot
 * of the products and their prices at the time of purchase, so price changes
 * after the order don't affect historical records.</p>
 *
 * <h3>Relationships:</h3>
 * <ul>
 *   <li>Many-to-One with {@link User} — a user can have many orders</li>
 *   <li>One-to-Many with {@link OrderItem} — an order contains multiple line items</li>
 * </ul>
 *
 * <h3>Payment flow:</h3>
 * <ol>
 *   <li>Order created with status PENDING</li>
 *   <li>Stripe checkout session is created, stripeSessionId is saved</li>
 *   <li>On successful payment webhook, status is updated to PAID</li>
 *   <li>Admin can then update to SHIPPED → DELIVERED</li>
 * </ol>
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_user", columnList = "user_id"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_stripe_session", columnList = "stripeSessionId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who placed this order */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Line items in this order. Each item captures the product, quantity,
     * and the price at the time of purchase.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    /** Total amount for the entire order (sum of all item subtotals) */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /** Current status of the order in its lifecycle */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    /** Shipping address provided by the customer at checkout */
    @Column(nullable = false, length = 500)
    private String shippingAddress;

    /**
     * Stripe Checkout Session ID — used to correlate webhook events
     * with this order for payment confirmation.
     */
    @Column(length = 255)
    private String stripeSessionId;

    /** When the order was placed */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** When the order was last updated (e.g., status change) */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
