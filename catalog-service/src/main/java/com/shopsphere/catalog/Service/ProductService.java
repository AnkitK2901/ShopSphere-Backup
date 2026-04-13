package com.shopsphere.catalog.Service;
import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import java.util.List;



public interface ProductService {
    Product createProduct(ProductRequestDTO dto);
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product updateProduct(Long id, ProductRequestDTO dto);
    void deleteProduct(Long id);

}

