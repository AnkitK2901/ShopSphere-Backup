package com.shopsphere.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.inventory.dto.StockRequest;
import com.shopsphere.inventory.model.InventoryItem;
import com.shopsphere.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @Test
    void testCheckStock_ReturnsTrue() throws Exception {
        StockRequest request = new StockRequest();
        request.setProductId("P101");
        request.setQuantity(5);
        Mockito.when(inventoryService.checkStock("P101", 5)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/inventory/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"));
    }

    @Test
    void addInventory_WhenRoleIsAdmin_ShouldReturnOk() throws Exception {
        InventoryItem item = new InventoryItem("P101", 10, "SUP_001", 5, 2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/inventory")
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void addInventory_WhenRoleIsNotAdmin_ShouldReturnForbidden() throws Exception {
        InventoryItem item = new InventoryItem("P101", 10, "SUP_001", 5, 2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/inventory")
                .header("X-User-Role", "ROLE_USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    // --- NEW TESTS ADDED FOR GET ALL INVENTORY ---

    @Test
    void getAllInventory_WhenRoleIsAdmin_ShouldReturnOkAndList() throws Exception {
        InventoryItem item = new InventoryItem("P101", 10, "SUP_001", 5, 2);
        Mockito.when(inventoryService.getAllInventory()).thenReturn(Arrays.asList(item));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory")
                .header("X-User-Role", "ROLE_ADMIN"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productId").value("P101"));
    }

    @Test
    void getAllInventory_WhenRoleIsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory")
                .header("X-User-Role", "ROLE_USER"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}