package com.shopsphere.inventory.service;

import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.exception.ResourceNotFoundException;
import com.shopsphere.inventory.model.InventoryItem;
import com.shopsphere.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<InventoryItem> getAllInventory() {
        log.info("Fetching all inventory items from the database");
        return inventoryRepository.findAll();
    }

    public boolean checkStock(Long productId, int quantity) {
        InventoryItem item = inventoryRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        return item.getStockLevel() >= quantity;
    }

    @Transactional
    public void addInventory(InventoryItem item) {
        inventoryRepository.findById(item.getProductId()).ifPresentOrElse(
                existing -> {
                    existing.setStockLevel(existing.getStockLevel() + item.getStockLevel());
                    if (item.getSupplierId() != null)
                        existing.setSupplierId(item.getSupplierId());
                    if (item.getReorderThreshold() != null)
                        existing.setReorderThreshold(item.getReorderThreshold());
                    if (item.getSupplierLeadTimeDays() != null)
                        existing.setSupplierLeadTimeDays(item.getSupplierLeadTimeDays());

                    inventoryRepository.save(existing);
                },
                () -> inventoryRepository.save(item));
    }

    @Transactional
    public void deductStock(Long productId, int quantity) {
        InventoryItem item = inventoryRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        if (item.getStockLevel() < quantity) {
            throw new InsufficientStockException("Not enough stock for product: " + productId);
        }

        item.setStockLevel(item.getStockLevel() - quantity);
        inventoryRepository.save(item);

        if (item.getReorderThreshold() != null && item.getStockLevel() <= item.getReorderThreshold()) {
            triggerAutoReorder(item);
        }
    }

    private void triggerAutoReorder(InventoryItem item) {
        log.warn("🚨 AUTO-REORDER ALERT: Stock for Product [{}] dropped to {}. (Threshold: {})",
                item.getProductId(), item.getStockLevel(), item.getReorderThreshold());

        int leadTime = item.getSupplierLeadTimeDays() != null ? item.getSupplierLeadTimeDays() : 7;

        log.info("📦 Automatically creating reorder ticket for Supplier [{}]. Estimated Lead Time: {} days.",
                item.getSupplierId(), leadTime);
    }

    // ==============================================================
    // SURGICAL FIXES: Added specifically for Ghost Inventory & SAGA
    // ==============================================================
    @Transactional
    public void initializeStock(Long productId, int stockLevel) {
        InventoryItem item = new InventoryItem();
        item.setProductId(productId);
        item.setStockLevel(stockLevel);
        item.setReorderThreshold(10); // Safe default
        inventoryRepository.save(item);
        log.info("Initialized stock for Product [{}] with {} units.", productId, stockLevel);
    }

    @Transactional
    public void refundStock(Long productId, int quantity) {
        InventoryItem item = inventoryRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        item.setStockLevel(item.getStockLevel() + quantity);
        inventoryRepository.save(item);
        log.info("SAGA ROLLBACK: Refunded {} units for Product [{}].", quantity, productId);
    }
}