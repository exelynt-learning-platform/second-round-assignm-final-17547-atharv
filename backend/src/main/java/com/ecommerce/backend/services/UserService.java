package com.ecommerce.backend.services;

import com.ecommerce.backend.dto.response.UserResponse;

import java.util.List;

/**
 * Service interface for user profile management.
 *
 * <p>Provides operations for viewing and updating user profiles.
 * Admin-specific operations (list all users, delete users) are also defined here.</p>
 */
public interface UserService {

    /**
     * Retrieves the profile of a specific user by their ID.
     *
     * @param id the user's database ID
     * @return UserResponse containing the user's profile (password excluded)
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if user not found
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves the profile of a user by their email address.
     * Used for fetching the current authenticated user's profile.
     *
     * @param email the user's email address
     * @return UserResponse containing the user's profile
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if user not found
     */
    UserResponse getUserByEmail(String email);

    /**
     * Retrieves all users in the system. Admin-only operation.
     *
     * @return list of all user profiles
     */
    List<UserResponse> getAllUsers();

    /**
     * Updates a user's profile information.
     * Only updates provided fields; password changes are handled separately.
     *
     * @param id        the user's ID
     * @param firstName updated first name (nullable — keeps existing if null)
     * @param lastName  updated last name (nullable)
     * @param phone     updated phone (nullable)
     * @param address   updated address (nullable)
     * @return updated UserResponse
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if user not found
     */
    UserResponse updateUser(Long id, String firstName, String lastName, String phone, String address);

    /**
     * Deletes a user account. Admin-only operation.
     *
     * @param id the user's ID
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if user not found
     */
    void deleteUser(Long id);
}
