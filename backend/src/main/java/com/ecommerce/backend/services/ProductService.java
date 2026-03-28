package com.ecommerce.backend.services;

import com.ecommerce.backend.dto.request.ProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;

import java.util.List;

/**
 * Service interface for product catalog management.
 *
 * <p>Provides CRUD operations for products and search/filter capabilities.
 * Create, update, and delete operations are restricted to admin users
 * at the controller/security level.</p>
 */
public interface ProductService {

    /**
     * Creates a new product in the catalog.
     *
     * @param request the product details
     * @return the created product
     */
    ProductResponse createProduct(ProductRequest request);

    /**
     * Retrieves all products in the catalog.
     *
     * @return list of all products
     */
    List<ProductResponse> getAllProducts();

    /**
     * Retrieves a specific product by its ID.
     *
     * @param id the product's database ID
     * @return the product details
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if product not found
     */
    ProductResponse getProductById(Long id);

    /**
     * Updates an existing product's information.
     *
     * @param id      the product's ID
     * @param request the updated product details
     * @return the updated product
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if product not found
     */
    ProductResponse updateProduct(Long id, ProductRequest request);

    /**
     * Deletes a product from the catalog.
     *
     * @param id the product's ID
     * @throws com.ecommerce.backend.exception.ResourceNotFoundException if product not found
     */
    void deleteProduct(Long id);

    /**
     * Searches products by name (case-insensitive partial match).
     *
     * @param keyword the search term
     * @return list of matching products
     */
    List<ProductResponse> searchByName(String keyword);

    /**
     * Filters products by category.
     *
     * @param category the category to filter by
     * @return list of products in the given category
     */
    List<ProductResponse> getByCategory(String category);
}
