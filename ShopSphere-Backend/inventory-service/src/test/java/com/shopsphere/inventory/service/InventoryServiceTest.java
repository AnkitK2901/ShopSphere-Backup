package com.shopsphere.inventory.service;

import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.exception.ResourceNotFoundException;
import com.shopsphere.inventory.model.InventoryItem;
import com.shopsphere.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService; // Assuming this points to the Impl class in your setup

    private InventoryItem mockItem;

    @BeforeEach
    void setUp() {
        mockItem = new InventoryItem("P101", 50, "SUP_001", 10, 5);
    }

    @Test
    void checkStock_WhenStockIsSufficient_ShouldReturnTrue() {
        when(inventoryRepository.findById("P101")).thenReturn(Optional.of(mockItem));
        boolean result = inventoryService.checkStock("P101", 20);
        assertTrue(result);
    }

    @Test
    void checkStock_WhenStockIsInsufficient_ShouldReturnFalse() {
        when(inventoryRepository.findById("P101")).thenReturn(Optional.of(mockItem));
        boolean result = inventoryService.checkStock("P101", 60);
        assertFalse(result);
    }

    @Test
    void checkStock_WhenProductNotFound_ShouldThrowException() {
        when(inventoryRepository.findById("P999")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            inventoryService.checkStock("P999", 10);
        });
    }

    @Test
    void deductStock_WhenStockIsSufficient_ShouldUpdateAndSave() {
        when(inventoryRepository.findById("P101")).thenReturn(Optional.of(mockItem));
        inventoryService.deductStock("P101", 10);
        assertEquals(40, mockItem.getStockLevel());
        verify(inventoryRepository, times(1)).save(mockItem);
    }

    @Test
    void deductStock_WhenStockIsInsufficient_ShouldThrowExceptionAndNotSave() {
        when(inventoryRepository.findById("P101")).thenReturn(Optional.of(mockItem));
        assertThrows(InsufficientStockException.class, () -> {
            inventoryService.deductStock("P101", 60);
        });
        assertEquals(50, mockItem.getStockLevel());
        verify(inventoryRepository, never()).save(any(InventoryItem.class));
    }

    @Test
    void addInventory_WhenItemExists_ShouldUpdateFieldsAndSave() {
        when(inventoryRepository.findById("P101")).thenReturn(Optional.of(mockItem));
        InventoryItem updateItem = new InventoryItem("P101", 20, "NEW_SUP", 15, 7);

        inventoryService.addInventory(updateItem);

        assertEquals(70, mockItem.getStockLevel()); // 50 original + 20 new
        assertEquals("NEW_SUP", mockItem.getSupplierId());
        verify(inventoryRepository, times(1)).save(mockItem);
    }

    @Test
    void addInventory_WhenItemDoesNotExist_ShouldSaveNewItem() {
        when(inventoryRepository.findById("P202")).thenReturn(Optional.empty());
        InventoryItem newItem = new InventoryItem("P202", 20, "SUP_002", 15, 7);

        inventoryService.addInventory(newItem);

        verify(inventoryRepository, times(1)).save(newItem);
    }

    // --- NEW TEST ADDED FOR GET ALL INVENTORY ---
    @Test
    void getAllInventory_ShouldReturnListOfItems() {
        List<InventoryItem> expectedList = Arrays.asList(mockItem);
        when(inventoryRepository.findAll()).thenReturn(expectedList);

        List<InventoryItem> actualList = inventoryService.getAllInventory();

        assertNotNull(actualList);
        assertEquals(1, actualList.size());
        assertEquals("P101", actualList.get(0).getProductId());
        verify(inventoryRepository, times(1)).findAll();
    }
}