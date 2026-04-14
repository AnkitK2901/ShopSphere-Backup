package com.shopsphere.inventory.service;

import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.model.InventoryItem;
import com.shopsphere.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        mockItem = new InventoryItem("P101", 50, "SUP_001", 10);
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
    void checkStock_WhenProductNotFound_ShouldReturnFalse() {
        when(inventoryRepository.findById("P999")).thenReturn(Optional.empty());
        boolean result = inventoryService.checkStock("P999", 10);
        assertFalse(result);
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
}