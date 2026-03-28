package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.ProductRequest;
import com.ecommerce.backend.dto.response.ApiResponse;
import com.ecommerce.backend.dto.response.ProductResponse;
import com.ecommerce.backend.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for product catalog management.
 *
 * <h3>Public endpoints (no authentication):</h3>
 * <ul>
 *   <li>{@code GET /api/products} — list all products</li>
 *   <li>{@code GET /api/products/{id}} — get product details</li>
 *   <li>{@code GET /api/products/search?keyword=} — search by name</li>
 *   <li>{@code GET /api/products/category/{category}} — filter by category</li>
 * </ul>
 *
 * <h3>Admin-only endpoints (configured in SecurityConfig):</h3>
 * <ul>
 *   <li>{@code POST /api/products} — create a product</li>
 *   <li>{@code PUT /api/products/{id}} — update a product</li>
 *   <li>{@code DELETE /api/products/{id}} — delete a product</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ======================== PUBLIC ENDPOINTS ========================

    /**
     * Lists all products in the catalog.
     * No authentication required — used for product browsing.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    /**
     * Retrieves a specific product by its ID.
     *
     * @param id the product's database ID
     * @return the product details or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
    }

    /**
     * Searches products by name (case-insensitive partial match).
     * Example: {@code GET /api/products/search?keyword=phone}
     *
     * @param keyword the search term
     * @return list of matching products
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @RequestParam String keyword) {
        List<ProductResponse> products = productService.searchByName(keyword);
        return ResponseEntity.ok(ApiResponse.success(products, "Search results retrieved"));
    }

    /**
     * Filters products by category.
     * Example: {@code GET /api/products/category/Electronics}
     *
     * @param category the category to filter by
     * @return list of products in the given category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getByCategory(
            @PathVariable String category) {
        List<ProductResponse> products = productService.getByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(products, "Products filtered by category"));
    }

    // ======================== ADMIN ENDPOINTS ========================

    /**
     * Creates a new product. ADMIN role required (enforced by SecurityConfig).
     *
     * @param request validated product data
     * @return 201 Created with the new product details
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Product created successfully"));
    }

    /**
     * Updates an existing product. ADMIN role required.
     *
     * @param id      the product's ID
     * @param request updated product data
     * @return the updated product details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }

    /**
     * Deletes a product from the catalog. ADMIN role required.
     *
     * @param id the product's ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }
}
