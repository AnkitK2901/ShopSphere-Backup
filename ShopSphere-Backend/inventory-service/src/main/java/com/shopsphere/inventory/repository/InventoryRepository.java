package com.shopsphere.inventory.repository;

import com.shopsphere.inventory.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// CHANGED String TO Long in JpaRepository signature
@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
}