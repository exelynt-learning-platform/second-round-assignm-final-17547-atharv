package com.ecommerce.backend.services.impl;

import com.ecommerce.backend.dto.request.PlaceOrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.entity.*;
import com.ecommerce.backend.entity.enums.OrderStatus;
import com.ecommerce.backend.exception.InsufficientStockException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.services.CartService;
import com.ecommerce.backend.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link OrderService} — handles order lifecycle management.
 *
 * <h3>Order creation flow ({@code placeOrder}):</h3>
 * <ol>
 *   <li>Fetch the user's cart and validate it has items</li>
 *   <li>For each cart item: validate stock, snapshot the price, decrement inventory</li>
 *   <li>Calculate total order amount</li>
 *   <li>Create the Order with OrderItems</li>
 *   <li>Clear the user's cart</li>
 *   <li>Return the order details</li>
 * </ol>
 *
 * <p>This entire operation runs in a single transaction. If any stock
 * validation fails, the entire operation rolls back — no partial orders.</p>
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository,
                            CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Override
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        // Step 1: Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Step 2: Fetch user's cart and validate it's not empty
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart is empty. Add items before placing an order."));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Add items before placing an order.");
        }

        // Step 3: Create the order entity
        Order order = Order.builder()
                .user(user)
                .shippingAddress(request.getShippingAddress())
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Step 4: Process each cart item
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // Validate stock availability
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(product.getName(), product.getStockQuantity());
            }

            // Decrement product stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Create order item with price snapshot (historical price at time of purchase)
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())  // Snapshot current price
                    .build();

            order.getOrderItems().add(orderItem);

            // Accumulate total: price × quantity
            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        // Step 5: Set the total amount and save the order
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        logger.info("Order placed: ID={}, User={}, Total={}", savedOrder.getId(), user.getEmail(), totalAmount);

        // Step 6: Clear the user's cart (items have been moved to the order)
        cartService.clearCart(userId);

        return OrderResponse.fromEntity(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Security check: ensure the order belongs to the requesting user
        if (!order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        return OrderResponse.fromEntity(order);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        logger.info("Order {} status updated to {}", orderId, status);

        return OrderResponse.fromEntity(updatedOrder);
    }
}
