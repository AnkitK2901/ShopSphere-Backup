package com.shopsphere.catalog.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long productId;
    private String name;
    private String description; // Safely added
    private Double basePrice;
    private String previewImage;
    private boolean isActive;
    private List<CustomOptionResponseDTO> customOptions;
}