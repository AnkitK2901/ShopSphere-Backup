package com.shopsphere.order.DTO;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockRequest {
    private String productId;
    private Integer quantity;
}