package com.ecommerce.backend.services;

import com.ecommerce.backend.dto.request.PlaceOrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.entity.enums.OrderStatus;

import java.util.List;

/**
 * Service interface for order processing.
 *
 * <p>Handles the complete order lifecycle: creation from cart,
 * retrieval, and status updates. Orders are user-scoped — users
 * can only view their own orders.</p>
 */
public interface OrderService {

    /**
     * Creates a new order from the user's cart contents.
     * Validates stock availability, decrements product stock,
     * creates order items with price snapshots, and clears the cart.
     *
     * @param userId  the authenticated user's ID
     * @param request contains the shipping address
     * @return the created order
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if cart is empty
     * @throws com.ecommerce.backend.exception.InsufficientStockException if any product has insufficient stock
     */
    OrderResponse placeOrder(Long userId, PlaceOrderRequest request);

    /**
     * Retrieves all orders for the current user, sorted by most recent first.
     *
     * @param userId the authenticated user's ID
     * @return list of the user's orders
     */
    List<OrderResponse> getOrdersByUser(Long userId);

    /**
     * Retrieves a specific order, ensuring it belongs to the requesting user.
     *
     * @param userId  the authenticated user's ID
     * @param orderId the order's ID
     * @return the order details
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if order not found or doesn't belong to user
     */
    OrderResponse getOrderById(Long userId, Long orderId);

    /**
     * Updates the status of an order. Admin-only operation.
     * Used for marking orders as SHIPPED, DELIVERED, or CANCELLED.
     *
     * @param orderId the order's ID
     * @param status  the new status
     * @return the updated order
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if order not found
     */
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
}
