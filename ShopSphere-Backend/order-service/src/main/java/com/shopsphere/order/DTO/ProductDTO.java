package com.shopsphere.order.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    // FIX: Changed from String to Long to match Catalog and Inventory
    private Long productId; 
    private String name;
    private String description;
    private double basePrice;
    private double totalPrice;
    private String previewImage;
    private List<CustomOptionDTO> customOptions;
}