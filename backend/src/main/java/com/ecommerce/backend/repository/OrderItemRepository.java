package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link OrderItem} entity.
 *
 * <p>Order items are typically managed through the Order entity's cascade
 * operations, but this repository is available for direct queries if needed.</p>
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Inherits standard CRUD methods from JpaRepository.
    // Order items are managed through Order entity's cascade operations.
}
