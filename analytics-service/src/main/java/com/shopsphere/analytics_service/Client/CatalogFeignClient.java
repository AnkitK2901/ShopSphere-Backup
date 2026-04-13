package com.shopsphere.analytics_service.Client;

import com.shopsphere.analytics_service.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", configuration = FeignConfig.class)
public interface CatalogFeignClient {

    record ProductResponse(
            Long productId,
            String name,            // matches ProductResponseDTO.name
            Double basePrice,       // matches ProductResponseDTO.basePrice
            Double totalPrice,      // matches ProductResponseDTO.totalPrice
            String previewImage     // matches ProductResponseDTO.previewImage
    ) {}

    // ⚠ VERIFY THIS PATH against your catalog-service controller
    @GetMapping("/api/products/{productId}")
    ProductResponse getProductById(@PathVariable("productId") Long productId);
}
