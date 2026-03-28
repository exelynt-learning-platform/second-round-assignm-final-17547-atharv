package com.ecommerce.backend.constant;

/**
 * Centralized message constants for API responses and error messages.
 *
 * <p>Using constants instead of inline strings ensures:</p>
 * <ul>
 *   <li>Consistency: Same message text across all controllers and services</li>
 *   <li>Maintainability: Change a message in one place, updated everywhere</li>
 *   <li>I18n readiness: Easy to replace with a message source for localization</li>
 * </ul>
 */
public final class MessageConstants {

    // Private constructor prevents instantiation of this utility class
    private MessageConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ======================== SUCCESS MESSAGES ========================

    public static final String SUCCESS = "Operation successful";
    public static final String RECORD_CREATED = "Record created successfully";
    public static final String RECORD_UPDATED = "Record updated successfully";
    public static final String RECORD_DELETED = "Record deleted successfully";
    public static final String RECORD_FETCHED = "Record fetched successfully";
    public static final String RECORDS_FETCHED = "Records fetched successfully";

    // ======================== AUTH MESSAGES ========================

    public static final String REGISTRATION_SUCCESS = "User registered successfully";
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String EMAIL_ALREADY_EXISTS = "A user with this email already exists";
    public static final String INVALID_CREDENTIALS = "Invalid email or password";

    // ======================== CART MESSAGES ========================

    public static final String ITEM_ADDED_TO_CART = "Item added to cart successfully";
    public static final String CART_ITEM_UPDATED = "Cart item updated successfully";
    public static final String CART_ITEM_REMOVED = "Item removed from cart";
    public static final String CART_CLEARED = "Cart cleared successfully";
    public static final String CART_EMPTY = "Cart is empty. Add items before placing an order.";

    // ======================== ORDER MESSAGES ========================

    public static final String ORDER_PLACED = "Order placed successfully";
    public static final String ORDER_STATUS_UPDATED = "Order status updated successfully";

    // ======================== PAYMENT MESSAGES ========================

    public static final String CHECKOUT_SESSION_CREATED = "Checkout session created successfully";
    public static final String PAYMENT_SUCCESS = "Payment processed successfully";
    public static final String PAYMENT_FAILED = "Payment processing failed";
    public static final String WEBHOOK_PROCESSED = "Webhook processed successfully";

    // ======================== ERROR MESSAGES ========================

    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";
    public static final String BAD_REQUEST = "Invalid request parameters";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String UNAUTHORIZED = "Unauthorized access";
    public static final String FORBIDDEN = "Access denied. You don't have permission to perform this action.";
    public static final String VALIDATION_FAILED = "Validation failed";
}