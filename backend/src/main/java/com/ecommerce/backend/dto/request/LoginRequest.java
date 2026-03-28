package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login requests.
 *
 * <p>Contains only the credentials needed for authentication.
 * The service layer validates these against stored (hashed) credentials
 * and returns a JWT token on success.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    /** Email address used as the login identifier */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    /** Raw password — compared against the BCrypt hash stored in the database */
    @NotBlank(message = "Password is required")
    private String password;
}
