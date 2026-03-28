package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Cart} entity.
 *
 * <p>Each user has exactly one cart, so we look up carts by user ID.
 * The Optional return type handles the case where a user hasn't added
 * any items yet (no cart created).</p>
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Find the cart belonging to a specific user.
     *
     * @param userId the user's ID
     * @return Optional containing the cart if it exists
     */
    Optional<Cart> findByUserId(Long userId);
}
