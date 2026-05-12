package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.StockRequest;
import com.shopsphere.inventory.model.InventoryItem;
import com.shopsphere.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
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
        log.info("Received POST request to check stock for Product: {}", request.getProductId());
        boolean inStock = inventoryService.checkStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(inStock);
    }

    @PostMapping
    public ResponseEntity<?> addInventory(
            @RequestBody com.shopsphere.inventory.model.InventoryItem item,
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            log.warn("Unauthorized attempt to add inventory by role: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only Administrators can add raw inventory.");
        }

        log.info("Received POST request to add inventory for Product: {}", item.getProductId());
        inventoryService.addInventory(item);
        return ResponseEntity.ok("Successfully added inventory for product: " + item.getProductId());
    }

    @PostMapping("/deduct")
    public ResponseEntity<String> deductStock(@Valid @RequestBody StockRequest request) {
        log.info("Received POST request to deduct stock for Product: {}", request.getProductId());
        inventoryService.deductStock(request.getProductId(), request.getQuantity());
        return ResponseEntity
                .ok("Successfully deducted " + request.getQuantity() + " items for product: " + request.getProductId());
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllInventory(@RequestHeader("X-User-Role") String role) {
        log.info("Received GET request to fetch all inventory from Role: {}", role);
        
        // FIX: Changed ROLE_SELLER to ROLE_ARTISAN to match Auth structure
        if (!"ROLE_ADMIN".equals(role) && !"ROLE_ARTISAN".equals(role)) {
            log.warn("Unauthorized access attempt to inventory by role: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<InventoryItem> items = inventoryService.getAllInventory();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/initialize")
    public ResponseEntity<Void> initializeStock(
            @RequestParam("productId") Long productId,
            @RequestParam("stockLevel") int stockLevel) {
        log.info("Received POST request to initialize stock for new Product: {}", productId);
        inventoryService.initializeStock(productId, stockLevel);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refund")
    public ResponseEntity<String> refundStock(@Valid @RequestBody StockRequest request) {
        log.info("Received POST request to refund stock for Product: {}", request.getProductId());
        inventoryService.refundStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Successfully refunded " + request.getQuantity() + " items for product: " + request.getProductId());
    }
}