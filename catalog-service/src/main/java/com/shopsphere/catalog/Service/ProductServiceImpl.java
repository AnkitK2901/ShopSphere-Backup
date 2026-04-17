package com.shopsphere.catalog.Service;

import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.Entity.CustomOption;
import com.shopsphere.catalog.Exception.ResourceNotFoundException;
import com.shopsphere.catalog.Repository.ProductRepository;
import com.shopsphere.catalog.Repository.CustomOptionRepository;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomOptionRepository optionRepository;

    @Override
    public Product createProduct(ProductRequestDTO dto) {
           logger.info("Creating product: {}", dto.getName());
           Product product = new Product();
           product.setName(dto.getName());
           product.setBasePrice(dto.getBasePrice());
           product.setPreviewImage(dto.getPreviewImage());
           product.setActive(true);

           if (dto.getSelectedOptionIds() != null && !dto.getSelectedOptionIds().isEmpty()) {
               List<CustomOption> options = optionRepository.findAllById(dto.getSelectedOptionIds());
               product.setCustomOptions(new LinkedHashSet<>(options));
           }
           return productRepository.save(product);
    }

   @Override
    public List<Product> getAllProducts() {
        logger.info("Fetching all ACTIVE products directly from DB");
        return productRepository.findByIsActiveTrue();
    }

    @Override
    public Product getProductById(Long id) {
        logger.info("Fetching product {} directly from DB", id);
        return productRepository.findByProductIdAndIsActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Active Product not found with id: " + id));
    }

    @Override
    public Product updateProduct(Long id, ProductRequestDTO dto) {
        logger.info("Updating product with id: {}", id);
        Product product = getProductById(id);
        product.setName(dto.getName());
        product.setBasePrice(dto.getBasePrice());
        product.setPreviewImage(dto.getPreviewImage());

        if (dto.getSelectedOptionIds() != null) {
            List<CustomOption> options = optionRepository.findAllById(dto.getSelectedOptionIds());
            product.setCustomOptions(new LinkedHashSet<>(options));
        }
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        logger.info("Deactivating product with id: {}", id);
        Product product = getProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }
}