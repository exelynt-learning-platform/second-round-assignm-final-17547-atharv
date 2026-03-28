package com.ecommerce.backend.constant;

/**
 * Centralized role constants used across the application.
 *
 * <h3>Important Spring Security convention:</h3>
 * <p>When using {@code hasRole("ADMIN")} in security config, Spring Security
 * automatically prepends "ROLE_" — so it checks for "ROLE_ADMIN" in the
 * user's authorities. That's why we store roles as "ROLE_ADMIN" in the database
 * but use "ADMIN" in {@code hasRole()} calls.</p>
 *
 * <p>These constants should be used when assigning roles to users
 * (e.g., during registration or admin user seeding).</p>
 */
public final class RoleConstants {

    // Private constructor prevents instantiation of this utility class
    private RoleConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /** Standard user role — can browse products, manage cart, place orders */
    public static final String USER = "ROLE_USER";

    /** Admin role — can manage products, view all users, update order statuses */
    public static final String ADMIN = "ROLE_ADMIN";
}