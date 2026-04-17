package com.shopsphere.catalog.Repository;

import com.shopsphere.catalog.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // --- NEW ADDITION: Fetch only active products for the storefront ---
    List<Product> findByIsActiveTrue();
    
    // --- NEW ADDITION: Prevent fetching a deleted product by ID ---
    Optional<Product> findByProductIdAndIsActiveTrue(Long id);
}