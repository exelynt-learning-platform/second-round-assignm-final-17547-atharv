package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Product} entity — provides CRUD and search/filter methods.
 *
 * <p>Query derivation automatically generates SQL from method names:</p>
 * <ul>
 *   <li>{@code findByCategory} → {@code WHERE category = ?}</li>
 *   <li>{@code findByNameContainingIgnoreCase} → {@code WHERE LOWER(name) LIKE LOWER('%?%')}</li>
 * </ul>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all products in a specific category.
     *
     * @param category the category to filter by (exact match)
     * @return list of products in the given category
     */
    List<Product> findByCategory(String category);

    /**
     * Search products by name (case-insensitive partial match).
     * Useful for implementing a search bar on the frontend.
     *
     * @param keyword the search keyword
     * @return list of products whose name contains the keyword
     */
    List<Product> findByNameContainingIgnoreCase(String keyword);
}
