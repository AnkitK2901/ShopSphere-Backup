package com.shopsphere.catalog.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryFeignClient {

    @PostMapping("/api/inventory/initialize")
    String initializeInventory(
            @RequestParam("productId") Long productId, 
            @RequestParam("stockLevel") Integer stockLevel
    );
}