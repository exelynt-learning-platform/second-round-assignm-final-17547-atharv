package com.ecommerce.backend.services;

import com.ecommerce.backend.dto.request.CartItemRequest;
import com.ecommerce.backend.dto.request.UpdateCartItemRequest;
import com.ecommerce.backend.dto.response.CartResponse;

/**
 * Service interface for shopping cart management.
 *
 * <p>All operations are scoped to a specific user — users can only
 * access and modify their own cart. The user ID is extracted from
 * the JWT token at the controller level.</p>
 */
public interface CartService {

    /**
     * Retrieves the current user's cart with all items.
     * Returns an empty cart if the user hasn't added any items yet.
     *
     * @param userId the authenticated user's ID
     * @return the user's cart with items and total price
     */
    CartResponse getCartByUser(Long userId);

    /**
     * Adds a product to the user's cart.
     * If the product is already in the cart, increments the quantity.
     * If the user has no cart yet, creates one automatically.
     *
     * @param userId  the authenticated user's ID
     * @param request contains productId and quantity
     * @return the updated cart
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if product not found
     * @throws com.ecommerce.backend.exception.InsufficientStockException if quantity exceeds stock
     */
    CartResponse addItemToCart(Long userId, CartItemRequest request);

    /**
     * Updates the quantity of an existing cart item.
     *
     * @param userId     the authenticated user's ID
     * @param cartItemId the cart item's ID
     * @param request    contains the new quantity
     * @return the updated cart
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if cart item not found
     */
    CartResponse updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartItemRequest request);

    /**
     * Removes a specific item from the user's cart.
     *
     * @param userId     the authenticated user's ID
     * @param cartItemId the cart item's ID to remove
     * @return the updated cart
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if cart item not found
     */
    CartResponse removeItemFromCart(Long userId, Long cartItemId);

    /**
     * Removes all items from the user's cart.
     *
     * @param userId the authenticated user's ID
     */
    void clearCart(Long userId);
}
