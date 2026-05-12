package com.shopsphere.order.DTO;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockRequest {
    private Long productId; // THE FIX: Aligned with Inventory and Catalog
    private Integer quantity;
}