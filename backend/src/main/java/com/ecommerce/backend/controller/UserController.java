package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.dto.response.UserResponse;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for user profile management.
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li>{@code GET /api/users/me} — get current user's profile</li>
 *   <li>{@code PUT /api/users/me} — update current user's profile</li>
 *   <li>{@code GET /api/users} — list all users (ADMIN only)</li>
 *   <li>{@code GET /api/users/{id}} — get specific user (ADMIN only)</li>
 *   <li>{@code DELETE /api/users/{id}} — delete a user (ADMIN only)</li>
 * </ul>
 *
 * <p>{@code @AuthenticationPrincipal} injects the authenticated User entity
 * directly from the SecurityContext, avoiding manual user lookup.</p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns the profile of the currently authenticated user.
     * Uses {@code @AuthenticationPrincipal} to get the user from the JWT token.
     *
     * @param currentUser the authenticated user (injected by Spring Security)
     * @return the user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal User currentUser) {
        UserResponse response = userService.getUserById(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Profile retrieved successfully"));
    }

    /**
     * Updates the currently authenticated user's profile.
     * Accepts a partial update via a Map — only provided fields are updated.
     *
     * @param currentUser the authenticated user
     * @param updates     map of fields to update (firstName, lastName, phone, address)
     * @return the updated profile
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Map<String, String> updates) {
        UserResponse response = userService.updateUser(
                currentUser.getId(),
                updates.get("firstName"),
                updates.get("lastName"),
                updates.get("phone"),
                updates.get("address")
        );
        return ResponseEntity.ok(ApiResponse.success(response, "Profile updated successfully"));
    }

    /**
     * Lists all registered users. Admin-only endpoint.
     * {@code @PreAuthorize} annotation enforces role check at the method level.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    /**
     * Retrieves a specific user by ID. Admin-only endpoint.
     *
     * @param id the user's database ID
     * @return the user's profile
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "User retrieved successfully"));
    }

    /**
     * Deletes a user account. Admin-only endpoint.
     *
     * @param id the user's database ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }
}
