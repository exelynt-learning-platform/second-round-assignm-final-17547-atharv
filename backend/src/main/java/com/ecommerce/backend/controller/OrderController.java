package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.PlaceOrderRequest;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.entity.enums.OrderStatus;
import com.ecommerce.backend.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for order management.
 *
 * <h3>User endpoints:</h3>
 * <ul>
 *   <li>{@code POST /api/orders} — place a new order from cart</li>
 *   <li>{@code GET /api/orders} — list user's own orders</li>
 *   <li>{@code GET /api/orders/{id}} — get specific order details</li>
 * </ul>
 *
 * <h3>Admin endpoints:</h3>
 * <ul>
 *   <li>{@code PUT /api/orders/{id}/status} — update order status (SHIPPED, DELIVERED, etc.)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Places a new order from the user's current cart contents.
     * Validates stock, decrements inventory, snapshots prices, and clears the cart.
     *
     * @param user    the authenticated user (from JWT)
     * @param request contains the shipping address
     * @return 201 Created with the new order details
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse order = orderService.placeOrder(user.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Order placed successfully"));
    }

    /**
     * Lists all orders for the current user, sorted by most recent first.
     *
     * @param user the authenticated user (from JWT)
     * @return list of the user's orders
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(
            @AuthenticationPrincipal User user) {
        List<OrderResponse> orders = orderService.getOrdersByUser(user.getId());
        return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
    }

    /**
     * Retrieves a specific order. Only the order's owner can view it.
     *
     * @param user    the authenticated user (from JWT)
     * @param orderId the order's ID
     * @return the order details or 404 if not found / not owned
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(user.getId(), orderId);
        return ResponseEntity.ok(ApiResponse.success(order, "Order retrieved successfully"));
    }

    /**
     * Updates the status of an order. Admin-only endpoint.
     * Used for marking orders as SHIPPED, DELIVERED, or CANCELLED.
     *
     * @param orderId the order's ID
     * @param status  the new status (passed as a query parameter)
     * @return the updated order
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        OrderResponse order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success(order, "Order status updated to " + status));
    }
}
