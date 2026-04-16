package com.shopsphere.analytics_service.Client;

import com.shopsphere.analytics_service.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", configuration = FeignConfig.class)
public interface CatalogFeignClient {

    record ProductResponse(
            Long productId,
            String name,
            Double basePrice,
            Double totalPrice,
            String previewImage
    ) {}

    @GetMapping("/api/products/{productId}")
    ProductResponse getProductById(@PathVariable("productId") String productId);
}