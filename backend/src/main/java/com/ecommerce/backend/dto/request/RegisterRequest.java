package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration requests.
 *
 * <p>Jakarta Bean Validation annotations ensure that invalid data
 * is rejected at the controller layer before reaching business logic.
 * Spring's {@code @Valid} annotation on the controller parameter triggers validation.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    /** User's first name — must not be blank */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    /** User's last name — must not be blank */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    /** Email address — must be valid format and unique (checked in service layer) */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    /** Password — minimum 6 characters, will be BCrypt-hashed before storage */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    /** Phone number — optional */
    private String phone;

    /** Shipping address — optional, can be set later */
    private String address;
}
