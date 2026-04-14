package com.shopsphere.catalog.Controller;

import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.Mapper.ProductMapper;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import com.shopsphere.catalog.ResponseDTO.ProductResponseDTO;
import com.shopsphere.catalog.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ProductResponseDTO> create(
            @Valid @RequestBody ProductRequestDTO dto,
            @RequestHeader("X-Logged-In-User") String sellerUsername) {
            
        log.info("Product creation initiated by seller: {}", sellerUsername);
        // Note: Ensure your ProductServiceImpl.createProduct is updated to accept sellerUsername!
        Product saved = productService.createProduct(dto, sellerUsername);
        return new ResponseEntity<>(ProductMapper.toDTO(saved), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        log.info("Received GET request to fetch all products in catalog");
        List<ProductResponseDTO> list = productService.getAllProducts()
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        log.info("Received GET request to fetch details for Product ID: {}", id);
        return ResponseEntity.ok(ProductMapper.toDTO(productService.getProductById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto,
            @RequestHeader("X-Logged-In-User") String sellerUsername) {
            
        log.info("Product update requested for Product ID: {} by seller: {}", id, sellerUsername);
        return ResponseEntity.ok(ProductMapper.toDTO(productService.updateProduct(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @RequestHeader("X-Logged-In-User") String sellerUsername) {
            
        log.info("Product deletion requested for Product ID: {} by seller: {}", id, sellerUsername);
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}