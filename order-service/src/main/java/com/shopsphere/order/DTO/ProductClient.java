package com.shopsphere.order.DTO;

import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    public ProductDTO findProductById(@PathVariable Long id);
}
