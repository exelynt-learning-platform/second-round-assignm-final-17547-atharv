package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Order} entity.
 *
 * <p>Provides methods for querying orders by user and by Stripe session ID
 * (used during payment webhook processing).</p>
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders for a specific user, sorted by most recent first.
     * Used for the "My Orders" page.
     *
     * @param userId the user's ID
     * @return list of orders sorted by creation date descending
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find an order by its Stripe Checkout Session ID.
     * Used in the Stripe webhook handler to update order status after payment.
     *
     * @param stripeSessionId the Stripe session ID
     * @return Optional containing the order if found
     */
    Optional<Order> findByStripeSessionId(String stripeSessionId);
}
