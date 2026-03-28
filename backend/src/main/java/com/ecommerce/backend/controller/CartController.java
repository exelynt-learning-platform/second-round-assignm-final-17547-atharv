package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CartItemRequest;
import com.ecommerce.backend.dto.request.UpdateCartItemRequest;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.dto.response.CartResponse;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.services.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for shopping cart management.
 *
 * <p>All endpoints are scoped to the authenticated user — users can only
 * access and modify their own cart. The user ID is extracted from the
 * JWT token via {@code @AuthenticationPrincipal}.</p>
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li>{@code GET /api/cart} — view current cart</li>
 *   <li>{@code POST /api/cart/items} — add a product to cart</li>
 *   <li>{@code PUT /api/cart/items/{itemId}} — update item quantity</li>
 *   <li>{@code DELETE /api/cart/items/{itemId}} — remove item from cart</li>
 *   <li>{@code DELETE /api/cart} — clear entire cart</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Retrieves the current user's shopping cart.
     * Returns an empty cart if no items have been added yet.
     *
     * @param user the authenticated user (from JWT)
     * @return the cart with all items and total price
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal User user) {
        CartResponse cart = cartService.getCartByUser(user.getId());
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart retrieved successfully"));
    }

    /**
     * Adds a product to the user's cart.
     * If the product is already in the cart, its quantity is incremented.
     *
     * @param user    the authenticated user (from JWT)
     * @param request contains productId and quantity
     * @return the updated cart
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CartItemRequest request) {
        CartResponse cart = cartService.addItemToCart(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(cart, "Item added to cart"));
    }

    /**
     * Updates the quantity of an existing cart item.
     *
     * @param user    the authenticated user (from JWT)
     * @param itemId  the cart item's ID
     * @param request contains the new quantity
     * @return the updated cart
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateCartItemQuantity(user.getId(), itemId, request);
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart item updated"));
    }

    /**
     * Removes a specific item from the user's cart.
     *
     * @param user   the authenticated user (from JWT)
     * @param itemId the cart item's ID to remove
     * @return the updated cart
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId) {
        CartResponse cart = cartService.removeItemFromCart(user.getId(), itemId);
        return ResponseEntity.ok(ApiResponse.success(cart, "Item removed from cart"));
    }

    /**
     * Clears all items from the user's cart.
     *
     * @param user the authenticated user (from JWT)
     * @return success message
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal User user) {
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared successfully"));
    }
}
