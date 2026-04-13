package com.shopsphere.catalog.Repository;

import com.shopsphere.catalog.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}

