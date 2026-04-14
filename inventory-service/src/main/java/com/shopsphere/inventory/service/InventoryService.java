package com.shopsphere.inventory.service;

import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.exception.ResourceNotFoundException;
import com.shopsphere.inventory.model.InventoryItem;
import com.shopsphere.inventory.repository.InventoryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean checkStock(String productId, Integer qty) {
        log.info("Checking stock for Product ID: {} | Requested Qty: {}", productId, qty);
        return inventoryRepository.findById(productId)
                .map(item -> item.getStockLevel() >= qty)
                .orElse(false);
    }

    @Transactional
    public void deductStock(String productId, Integer qty) {
        log.info("Attempting to deduct {} items from Product ID: {}", qty, productId);
        InventoryItem item = inventoryRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Deduction failed. Product ID {} does not exist in the database.", productId);
                    return new ResourceNotFoundException("Product ID not found: " + productId);
                });

        if (item.getStockLevel() < qty) {
            log.warn("Insufficient stock for Product ID: {}. Requested: {}, Available: {}",
                    productId, qty, item.getStockLevel());
            throw new InsufficientStockException("Not enough stock available!");
        }

        item.setStockLevel(item.getStockLevel() - qty);
        inventoryRepository.save(item);
        log.info("Successfully deducted stock. Product ID: {} | New Stock Level: {}",
                productId, item.getStockLevel());
    }

    // ADDED: Auto-reorder feature from the LLD requirements
    // Runs every 60 seconds (60000ms) for testing/demonstration purposes
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processAutoReorder() {
        log.info("[Scheduler] Running Auto-Reorder Check...");
        List<InventoryItem> lowStockItems = inventoryRepository.findItemsRequiringReorder();

        if (lowStockItems.isEmpty()) {
            log.info("[Scheduler] All stock levels are healthy.");
            return;
        }

        for (InventoryItem item : lowStockItems) {
            log.warn(
                    "[Scheduler] Auto-reordering Product ID: {} from Supplier: {}. Current Stock: {}, Threshold: {}. Estimated lead time: {} days.",
                    item.getProductId(), item.getSupplierId(), item.getStockLevel(), item.getReorderThreshold(),
                    item.getSupplierLeadTimeDays());

            // In a real scenario, this would call a Supplier API/Kafka Topic.
            // For simulation, we assume immediate fulfillment of +50 units.
            item.setStockLevel(item.getStockLevel() + 50);
            inventoryRepository.save(item);

            log.info("[Scheduler] Successfully requested restock for Product ID: {}", item.getProductId());
        }
    }

    // Add this method inside InventoryService.java
    @Transactional
    public void refundStock(String productId, Integer qty) {
        log.info("Rolling back/Refunding {} items to Product ID: {}", qty, productId);
        InventoryItem item = inventoryRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        item.setStockLevel(item.getStockLevel() + qty);
        inventoryRepository.save(item);
    }
}