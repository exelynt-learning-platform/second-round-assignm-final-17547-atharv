package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a product.
 *
 * <p>Used by admin endpoints for product CRUD operations.
 * Validation ensures data integrity before persistence.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    /** Product display name */
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    /** Detailed description of the product */
    private String description;

    /** Unit price — must be positive, uses BigDecimal for precision */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    /** Available stock — must be zero or positive */
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    /** URL to the product image */
    private String imageUrl;

    /** Category for product filtering (e.g., "Electronics", "Clothing") */
    private String category;
}
