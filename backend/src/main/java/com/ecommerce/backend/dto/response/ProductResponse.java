package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for product responses.
 *
 * <p>Maps all public product fields for API output. The {@code fromEntity}
 * factory method keeps Entity → DTO conversion logic in one place.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Converts a Product entity to a ProductResponse DTO.
     *
     * @param product the Product entity
     * @return a populated ProductResponse DTO
     */
    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .category(product.getCategory())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
