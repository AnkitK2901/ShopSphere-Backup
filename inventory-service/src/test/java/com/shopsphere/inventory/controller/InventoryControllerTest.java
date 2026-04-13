package com.shopsphere.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.inventory.dto.StockRequest;
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
}