package com.shopsphere.catalog.Service;

import com.shopsphere.catalog.Client.InventoryFeignClient;
import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.Entity.CustomOption;
import com.shopsphere.catalog.Exception.ResourceNotFoundException;
import com.shopsphere.catalog.Repository.ProductRepository;
import com.shopsphere.catalog.Repository.CustomOptionRepository;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict; // ADDED
import org.springframework.cache.annotation.Cacheable; // ADDED
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomOptionRepository optionRepository;

    @Autowired(required = false)
    private InventoryFeignClient inventoryClient;

    @Override
    @CacheEvict(value = "products", allEntries = true) // Clear list cache on new product
    public Product createProduct(ProductRequestDTO dto) {
        logger.info("Creating product: {}", dto.getName());
        Product product = new Product();
        product.setName(dto.getName());
        product.setBasePrice(dto.getBasePrice());
        product.setPreviewImage(dto.getPreviewImage());
        product.setActive(true);
        product.setDescription(dto.getDescription());
        if (dto.getSelectedOptionIds() != null && !dto.getSelectedOptionIds().isEmpty()) {
            List<CustomOption> options = optionRepository.findAllById(dto.getSelectedOptionIds());
            product.setCustomOptions(new LinkedHashSet<>(options));
        }
        Product savedProduct = productRepository.save(product);

        try {
            if (inventoryClient != null) {
                Integer initialStock = (dto.getStockLevel() != null) ? dto.getStockLevel() : 0;
                inventoryClient.initializeInventory(savedProduct.getProductId(), initialStock);
                logger.info("Stock initialized via Feign for product: {} with quantity: {}", savedProduct.getProductId(), initialStock);
            }
        } catch (Exception e) {
            logger.error("WARNING: Feign call failed to auto-initialize stock for Product ID {}. Cause: {}", savedProduct.getProductId(), e.getMessage());
        }

        return savedProduct;
    }

    @Override
    @Cacheable(value = "products") // Loads products from RAM for sub-1s latency
    public List<Product> getAllProducts() {
        logger.info("Fetching all ACTIVE products from DB/Cache");
        return productRepository.findByIsActiveTrue().stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "product", key = "#id") // Caches specific product details
    public Product getProductById(Long id) {
        logger.info("Fetching product {} from DB/Cache", id);
        return productRepository.findByProductIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active Product not found with id: " + id));
    }

    @Override
    @CacheEvict(value = {"products", "product"}, allEntries = true) // Purge cache on update
    public Product updateProduct(Long id, ProductRequestDTO dto) {
        logger.info("Updating product with id: {}", id);
        Product product = getProductById(id);
        product.setName(dto.getName());
        product.setBasePrice(dto.getBasePrice());
        product.setPreviewImage(dto.getPreviewImage());
        product.setDescription(dto.getDescription());
        if (dto.getSelectedOptionIds() != null) {
            List<CustomOption> options = optionRepository.findAllById(dto.getSelectedOptionIds());
            product.setCustomOptions(new LinkedHashSet<>(options));
        }
        return productRepository.save(product);
    }

    @Override
    @CacheEvict(value = {"products", "product"}, allEntries = true) // Purge cache on delete
    public void deleteProduct(Long id) {
        logger.info("Deactivating product with id: {}", id);
        Product product = getProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }
}