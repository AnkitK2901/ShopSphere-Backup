package com.shopsphere.catalog.Controller;

import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.Mapper.ProductMapper;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import com.shopsphere.catalog.ResponseDTO.ProductResponseDTO;
import com.shopsphere.catalog.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ProductResponseDTO> create(@RequestBody ProductRequestDTO dto) {
        // Service now takes the DTO directly to handle ID-based lookup
        Product savedProduct = productService.createProduct(dto);
        return new ResponseEntity<>(ProductMapper.toDTO(savedProduct), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        List<ProductResponseDTO> products = productService.getAllProducts()
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ProductMapper.toDTO(productService.getProductById(id)));
    }

}