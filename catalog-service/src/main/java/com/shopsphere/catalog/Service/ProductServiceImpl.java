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
    @CacheEvict(value = "products", allEntries = true) // Clears cache when new item is added
    public Product createProduct(ProductRequestDTO dto) {
        log.info("Creating product: {}", dto.getName());
        Product product = new Product();
        product.setName(dto.getName());
        product.setBasePrice(dto.getBasePrice());
        product.setPreviewImage(dto.getPreviewImage());

        if (dto.getSelectedOptionIds() != null && !dto.getSelectedOptionIds().isEmpty()) {
            List<CustomOption> options = optionRepository.findAllById(dto.getSelectedOptionIds());
            product.setCustomOptions(options);
        }
        return productRepository.save(product);
    }

    @Override
    @Cacheable(value = "products") // Caches the entire catalog for lightning-fast loading
    public List<Product> getAllProducts() {
        log.info("Fetching all products from Database (Cache Miss)");
        return productRepository.findAll();
    }

    @Override
    @Cacheable(value = "products", key = "#id") // Caches individual product lookups
    public Product getProductById(Long id) {
        log.info("Fetching product with id: {} from Database (Cache Miss)", id);
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    @CacheEvict(value = "products", allEntries = true) // Clears cache to prevent stale data
    public Product updateProduct(Long id, ProductRequestDTO dto) {
        log.info("Updating product with id: {}", id);
        Product product = getProductById(id);
        product.setName(dto.getName());
        product.setBasePrice(dto.getBasePrice());
        product.setPreviewImage(dto.getPreviewImage());

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