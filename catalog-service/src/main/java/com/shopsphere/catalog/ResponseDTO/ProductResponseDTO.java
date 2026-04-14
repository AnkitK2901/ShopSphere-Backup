package com.shopsphere.catalog.ResponseDTO;

import lombok.Data;
import java.util.List;

@Data
public class ProductResponseDTO {
    private Long productId;
    private String name;
    private Double basePrice;
    private Double totalPrice;
    private String previewImage;
    
    // --- LLD REQUIRED: Multi-Vendor and Regional Support ---
    private String vendorId;
    private String region;
    // -------------------------------------------------------

    private List<CustomOptionResponseDTO> customOptions;
}