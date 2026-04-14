package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.StockRequest;
import com.shopsphere.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@Slf4j
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    @PostMapping("/check")
    public ResponseEntity<Boolean> checkStock(@Valid @RequestBody StockRequest request) {
        log.info("Internal System: Checking stock for Product ID: {}, Requested Quantity: {}", request.getProductId(), request.getQuantity());
        boolean inStock = inventoryService.checkStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(inStock);
    }

    @PostMapping("/deduct")
    public ResponseEntity<String> deductStock(@Valid @RequestBody StockRequest request) {
        log.info("Internal System: Deducting stock for Product ID: {}, Quantity: {}", request.getProductId(), request.getQuantity());
        inventoryService.deductStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Successfully deducted " + request.getQuantity() + " items for product: " + request.getProductId());
    }


    @PutMapping("/update")
    public ResponseEntity<String> updateStock(
            @Valid @RequestBody StockRequest request,
            @RequestHeader("X-Logged-In-User") String artisanUsername) {
            
        log.info("Stock level update requested by Artisan: {} for Product ID: {}", artisanUsername, request.getProductId());
        inventoryService.updateStock(request, artisanUsername);
        return ResponseEntity.ok("Stock updated successfully for product: " + request.getProductId());
    }
}