package com.ecommerce.backend.services.impl;

import com.ecommerce.backend.dto.request.CartItemRequest;
import com.ecommerce.backend.dto.request.UpdateCartItemRequest;
import com.ecommerce.backend.dto.response.CartResponse;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.CartItem;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.InsufficientStockException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.services.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Implementation of {@link CartService} — manages shopping cart operations.
 *
 * <h3>Key behaviors:</h3>
 * <ul>
 *   <li>Auto-creates a cart for users who don't have one yet</li>
 *   <li>If a product is already in the cart, adding it again increments the quantity</li>
 *   <li>Stock validation ensures users can't add more than available inventory</li>
 *   <li>All operations are user-scoped via userId parameter</li>
 * </ul>
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByUser(Long userId) {
        // Find existing cart or return an empty cart response
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) {
            // Return an empty cart response if no cart exists yet
            return CartResponse.builder()
                    .items(new ArrayList<>())
                    .totalPrice(java.math.BigDecimal.ZERO)
                    .itemCount(0)
                    .build();
        }
        return CartResponse.fromEntity(cart);
    }

    @Override
    public CartResponse addItemToCart(Long userId, CartItemRequest request) {
        // Step 1: Fetch the product and validate it exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        // Step 2: Validate stock availability
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(product.getName(), product.getStockQuantity());
        }

        // Step 3: Get or create the user's cart
        Cart cart = getOrCreateCart(userId);

        // Step 4: Check if this product is already in the cart
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.getProductId());

        if (existingItem.isPresent()) {
            // Product already in cart — update quantity (increment)
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            // Validate that the total quantity doesn't exceed stock
            if (newQuantity > product.getStockQuantity()) {
                throw new InsufficientStockException(product.getName(), product.getStockQuantity());
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
            logger.debug("Updated cart item quantity for product: {} to {}", product.getName(), newQuantity);
        } else {
            // New product — create a new cart item
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
            cartRepository.save(cart);
            logger.debug("Added new product to cart: {}", product.getName());
        }

        // Step 5: Reload and return the updated cart
        Cart updatedCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        return CartResponse.fromEntity(updatedCart);
    }

    @Override
    public CartResponse updateCartItemQuantity(Long userId, Long cartItemId,
                                                UpdateCartItemRequest request) {
        // Fetch the cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        // Verify the item belongs to the user's cart (security check)
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("CartItem", "id", cartItemId);
        }

        // Validate stock for the new quantity
        Product product = cartItem.getProduct();
        if (request.getQuantity() > product.getStockQuantity()) {
            throw new InsufficientStockException(product.getName(), product.getStockQuantity());
        }

        // Update the quantity
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        logger.debug("Cart item {} quantity updated to {}", cartItemId, request.getQuantity());

        // Return updated cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        return CartResponse.fromEntity(cart);
    }

    @Override
    public CartResponse removeItemFromCart(Long userId, Long cartItemId) {
        // Fetch the cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        // Verify the item belongs to the user's cart (security check)
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("CartItem", "id", cartItemId);
        }

        // Remove the item from the cart's item list (triggers orphanRemoval)
        Cart cart = cartItem.getCart();
        cart.getItems().remove(cartItem);
        cartRepository.save(cart);
        logger.debug("Removed cart item {} from user {}'s cart", cartItemId, userId);

        return CartResponse.fromEntity(cart);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null) {
            cart.getItems().clear();  // orphanRemoval will delete all CartItem records
            cartRepository.save(cart);
            logger.debug("Cleared cart for user {}", userId);
        }
    }

    // ======================== PRIVATE HELPER ========================

    /**
     * Gets the existing cart for a user, or creates a new empty cart.
     * This ensures we never fail when a user adds their first item.
     */
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

            Cart newCart = Cart.builder()
                    .user(user)
                    .items(new ArrayList<>())
                    .build();

            Cart savedCart = cartRepository.save(newCart);
            logger.info("Created new cart for user: {}", user.getEmail());
            return savedCart;
        });
    }
}
