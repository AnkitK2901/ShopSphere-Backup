package com.shopsphere.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockRequest {

    @NotNull(message = "Product ID is required")
    private Long productId; // CHANGED FROM String TO Long

    @NotNull
    @Min(1)
    private Integer quantity;
}