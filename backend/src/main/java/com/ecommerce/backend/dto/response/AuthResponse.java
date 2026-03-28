package com.ecommerce.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO returned after successful authentication (login or registration).
 *
 * <p>Contains the JWT token that the client must include in subsequent
 * requests via the {@code Authorization: Bearer <token>} header.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /** JWT access token for authenticating subsequent API requests */
    private String token;

    /** Token type — always "Bearer" for JWT */
    @Builder.Default
    private String tokenType = "Bearer";

    /** Database ID of the authenticated user */
    private Long userId;

    /** Email of the authenticated user */
    private String email;

    /** Full name of the authenticated user */
    private String fullName;

    /** Role assigned to the user (e.g., "ROLE_USER", "ROLE_ADMIN") */
    private String role;
}
