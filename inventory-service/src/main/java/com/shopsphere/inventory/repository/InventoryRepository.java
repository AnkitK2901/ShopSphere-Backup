package com.shopsphere.inventory.repository;

import com.shopsphere.inventory.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryItem, String> {

    // ADDED: Query to fetch items that need auto-reordering
    @Query("SELECT i FROM InventoryItem i WHERE i.stockLevel <= i.reorderThreshold")
    List<InventoryItem> findItemsRequiringReorder();
}