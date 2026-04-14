package com.shopsphere.catalog.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Base price is required")
    private Double basePrice;

    private String previewImage;

    // --- LLD REQUIRED: Multi-Vendor and Regional Support ---
    @NotBlank(message = "Vendor ID (Artisan ID) is required")
    private String vendorId;

    @NotBlank(message = "Region is required for the catalog")
    private String region;
    // -------------------------------------------------------

    private List<Long> selectedOptionIds;
}