package com.shopsphere.catalog.Service;

import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.Entity.CustomOption;
import com.shopsphere.catalog.Repository.ProductRepository;
import com.shopsphere.catalog.Repository.CustomOptionRepository;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomOptionRepository optionRepository;

    @Override
    @Transactional
    public Product createProduct(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setBasePrice(dto.getBasePrice());
        product.setPreviewImage(dto.getPreviewImage());

        // Fetch Master Options by the IDs provided by the user
        if (dto.getSelectedOptionIds() != null && !dto.getSelectedOptionIds().isEmpty()) {
            List<CustomOption> selected = optionRepository.findAllById(dto.getSelectedOptionIds());
            product.setCustomOptions(selected);
        }

        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
}