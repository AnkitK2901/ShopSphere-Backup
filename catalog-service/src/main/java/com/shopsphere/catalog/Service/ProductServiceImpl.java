package com.shopsphere.catalog.Service;

import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.Entity.CustomOption;
import com.shopsphere.catalog.Exception.ResourceNotFoundException;
import com.shopsphere.catalog.Repository.ProductRepository;
import com.shopsphere.catalog.Repository.CustomOptionRepository;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CustomOptionRepository optionRepository;

    @Override
    public Product createProduct(ProductRequestDTO dto, String sellerUsername) {
        log.info("Seller {} is creating product: {}", sellerUsername, dto.getName());
        Product product = new Product();
        product.setName(dto.getName());
        product.setBasePrice(dto.getBasePrice());
        product.setPreviewImage(dto.getPreviewImage());
        
        // TODO: Ensure Product entity has a sellerUsername field to save this ownership!
        // product.setSellerUsername(sellerUsername);

        if (dto.getSelectedOptionIds() != null && !dto.getSelectedOptionIds().isEmpty()) {
            List<CustomOption> options = optionRepository.findAllById(dto.getSelectedOptionIds());
            product.setCustomOptions(options);
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product updateProduct(Long id, ProductRequestDTO dto, String sellerUsername) {
        log.info("Seller {} is updating product with id: {}", sellerUsername, id);
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
    public void deleteProduct(Long id, String sellerUsername) {
        log.info("Seller {} is deleting product with id: {}", sellerUsername, id);
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}