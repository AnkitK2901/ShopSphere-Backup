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
    private InventoryService inventoryService; 

    private InventoryItem mockItem;

    @BeforeEach
    void setUp() {
        // FIX: Changed "P101" to 101L
        mockItem = new InventoryItem(101L, 50, "SUP_001", 10, 5, 0L);
    }

    @Test
    void checkStock_WhenStockIsSufficient_ShouldReturnTrue() {
        when(inventoryRepository.findById(101L)).thenReturn(Optional.of(mockItem)); // FIX
        boolean result = inventoryService.checkStock(101L, 20); // FIX
        assertTrue(result);
    }

    @Test
    void checkStock_WhenStockIsInsufficient_ShouldReturnFalse() {
        when(inventoryRepository.findById(101L)).thenReturn(Optional.of(mockItem)); // FIX
        boolean result = inventoryService.checkStock(101L, 60); // FIX
        assertFalse(result);
    }

    @Test
    void checkStock_WhenProductNotFound_ShouldThrowException() {
        when(inventoryRepository.findById(999L)).thenReturn(Optional.empty()); // FIX
        assertThrows(ResourceNotFoundException.class, () -> {
            inventoryService.checkStock(999L, 10); // FIX
        });
    }

    @Test
    void deductStock_WhenStockIsSufficient_ShouldUpdateAndSave() {
        when(inventoryRepository.findById(101L)).thenReturn(Optional.of(mockItem)); // FIX
        inventoryService.deductStock(101L, 10); // FIX
        assertEquals(40, mockItem.getStockLevel());
        verify(inventoryRepository, times(1)).save(mockItem);
    }

    @Test
    void deductStock_WhenStockIsInsufficient_ShouldThrowExceptionAndNotSave() {
        when(inventoryRepository.findById(101L)).thenReturn(Optional.of(mockItem)); // FIX
        assertThrows(InsufficientStockException.class, () -> {
            inventoryService.deductStock(101L, 60); // FIX
        });
        assertEquals(50, mockItem.getStockLevel());
        verify(inventoryRepository, never()).save(any(InventoryItem.class));
    }

    @Test
    void addInventory_WhenItemExists_ShouldUpdateFieldsAndSave() {
        when(inventoryRepository.findById(101L)).thenReturn(Optional.of(mockItem)); // FIX
        
        InventoryItem updateItem = new InventoryItem(101L, 20, "NEW_SUP", 15, 7, 0L); // FIX

        inventoryService.addInventory(updateItem);

        assertEquals(70, mockItem.getStockLevel()); 
        assertEquals("NEW_SUP", mockItem.getSupplierId());
        verify(inventoryRepository, times(1)).save(mockItem);
    }

    @Test
    void addInventory_WhenItemDoesNotExist_ShouldSaveNewItem() {
        when(inventoryRepository.findById(202L)).thenReturn(Optional.empty()); // FIX
        
        InventoryItem newItem = new InventoryItem(202L, 20, "SUP_002", 15, 7, 0L); // FIX

        inventoryService.addInventory(newItem);

        verify(inventoryRepository, times(1)).save(newItem);
    }

    @Test
    void getAllInventory_ShouldReturnListOfItems() {
        List<InventoryItem> expectedList = Arrays.asList(mockItem);
        when(inventoryRepository.findAll()).thenReturn(expectedList);

        List<InventoryItem> actualList = inventoryService.getAllInventory();

        assertNotNull(actualList);
        assertEquals(1, actualList.size());
        assertEquals(101L, actualList.get(0).getProductId()); // FIX
        verify(inventoryRepository, times(1)).findAll();
    }
}