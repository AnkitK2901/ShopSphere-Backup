package com.shopsphere.catalog.Service;

import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.Entity.CustomOption;
import com.shopsphere.catalog.Exception.ResourceNotFoundException;
import com.shopsphere.catalog.Repository.ProductRepository;
import com.shopsphere.catalog.Repository.CustomOptionRepository;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CustomOptionRepository optionRepository;

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(ProductRequestDTO dto) {
        log.info("Creating product: {} for Vendor: {}", dto.getName(), dto.getVendorId());
        Product product = new Product();
        product.setName(dto.getName());
        product.setBasePrice(dto.getBasePrice());
        product.setPreviewImage(dto.getPreviewImage());
        
        // --- Setting the new fields ---
        product.setVendorId(dto.getVendorId());
        product.setRegion(dto.getRegion());
        // ------------------------------

        if (dto.getSelectedOptionIds() != null && !dto.getSelectedOptionIds().isEmpty()) {
            List<CustomOption> options = optionRepository.findAllById(dto.getSelectedOptionIds());
            product.setCustomOptions(options);
        }
        return productRepository.save(product);
    }

    @Override
    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        log.info("Fetching all products from Database (Cache Miss)");
        return productRepository.findAll();
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        log.info("Fetching product with id: {} from Database (Cache Miss)", id);
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Long id, ProductRequestDTO dto) {
        log.info("Updating product with id: {}", id);
        Product product = getProductById(id);
        product.setName(dto.getName());
        product.setBasePrice(dto.getBasePrice());
        product.setPreviewImage(dto.getPreviewImage());
        
        // --- Setting the new fields ---
        product.setVendorId(dto.getVendorId());
        product.setRegion(dto.getRegion());
        // ------------------------------

        if (dto.getSelectedOptionIds() != null) {
            List<CustomOption> options = optionRepository.findAllById(dto.getSelectedOptionIds());
            product.setCustomOptions(options);
        }
        return productRepository.save(product);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}