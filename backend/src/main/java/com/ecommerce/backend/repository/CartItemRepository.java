package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link CartItem} entity.
 *
 * <p>Provides methods to find and manage individual items within a cart.</p>
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find a specific product within a specific cart.
     * Used to check if a product is already in the cart before adding
     * (to increment quantity instead of creating a duplicate entry).
     *
     * @param cartId    the cart's ID
     * @param productId the product's ID
     * @return Optional containing the cart item if the product is in the cart
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Delete all items from a specific cart.
     * Used when clearing the cart or after placing an order.
     *
     * @param cartId the cart's ID
     */
    void deleteByCartId(Long cartId);
}
