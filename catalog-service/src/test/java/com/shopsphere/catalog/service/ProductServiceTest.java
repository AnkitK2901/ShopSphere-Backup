package com.shopsphere.catalog.service;

import com.shopsphere.catalog.Exception.ResourceNotFoundException;
import com.shopsphere.catalog.Service.ProductService;
import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("Test: Get Product by ID - Success Path")
    void testSuccess() {
        Product product = productService.getProductById(1L);
        assertNotNull(product);
        assertEquals(1L, product.getProductId());
    }

    @Test
    @DisplayName("Test: Get Product by ID - Exception Path")
    void testException() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(999L);
        });
    }
    @Test
    @DisplayName("Test: Create Product and Verify Pricing Logic")
    void testCreateProductAndPricing() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Unit Test Hoodie");
        dto.setBasePrice(1000.0);
        dto.setSelectedOptionIds(List.of(2L));
        Product savedProduct = productService.createProduct(dto);

        assertNotNull(savedProduct.getProductId());
        assertEquals("Unit Test Hoodie", savedProduct.getName());
        assertEquals(1150.0, savedProduct.getTotalPrice(), "Total price calculation is incorrect");
    }

    @Test
    @DisplayName("Test: Update Product Details")
    void testUpdateProduct() {
        ProductRequestDTO updateDto = new ProductRequestDTO();
        updateDto.setName("Updated Premium Hoodie");
        updateDto.setBasePrice(2000.0);
        updateDto.setSelectedOptionIds(List.of()); // Clear options
        Product updated = productService.updateProduct(1L, updateDto);

        assertEquals("Updated Premium Hoodie", updated.getName());
        assertEquals(2000.0, updated.getTotalPrice());
    }

    @Test
    @DisplayName("Test: Fetch All Products")
    void testGetAllProducts() {
        List<Product> list = productService.getAllProducts();

        assertNotNull(list);
        assertTrue(list.size() >= 2, "Should retrieve at least 2 products from data.sql");
    }

    @Test
    @DisplayName("Test: Delete Product")
    void testDeleteProduct() {
        productService.deleteProduct(2L);
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(2L);
        });
    }
}