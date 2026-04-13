package com.shopsphere.catalog.service;

import com.shopsphere.catalog.Exception.ResourceNotFoundException;
import com.shopsphere.catalog.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Test
    void testSuccess()
    {
        assertNotNull(productService.getProductById(1L));
    }
    @Test
    void testException()
    {
        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(999L));
    }
}