package com.shopsphere.catalog.Controller;

import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.Mapper.ProductMapper;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import com.shopsphere.catalog.ResponseDTO.ProductResponseDTO;
import com.shopsphere.catalog.Service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Catalog Management", description = "APIs for managing products and customizations")
public class ProductController {
    
    private final ProductService productService;

    @Operation(summary = "Create a new product")
    @PostMapping("/create")
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO dto) {
        log.info("REST request to create a product");
        Product saved = productService.createProduct(dto);
        return new ResponseEntity<>(ProductMapper.toDTO(saved), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all products (Cached)")
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        log.info("REST request to fetch all products");
        List<ProductResponseDTO> list = productService.getAllProducts()
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get a specific product by ID (Cached)")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        log.info("REST request to fetch product ID: {}", id);
        return ResponseEntity.ok(ProductMapper.toDTO(productService.getProductById(id)));
    }

    @Operation(summary = "Update an existing product")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody ProductRequestDTO dto) {
        log.info("REST request to update product ID: {}", id);
        return ResponseEntity.ok(ProductMapper.toDTO(productService.updateProduct(id, dto)));
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.info("REST request to delete product ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}