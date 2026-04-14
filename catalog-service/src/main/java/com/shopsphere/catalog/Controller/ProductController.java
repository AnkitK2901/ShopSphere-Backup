package com.shopsphere.catalog.Controller;
import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.Mapper.ProductMapper;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import com.shopsphere.catalog.ResponseDTO.ProductResponseDTO;
import com.shopsphere.catalog.Service.ProductService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO dto) {
        Product saved = productService.createProduct(dto);
        return new ResponseEntity<>(ProductMapper.toDTO(saved), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        List<ProductResponseDTO> list = productService.getAllProducts()
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ProductMapper.toDTO(productService.getProductById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.ok(ProductMapper.toDTO(productService.updateProduct(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}