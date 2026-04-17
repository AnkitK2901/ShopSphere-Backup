package com.shopsphere.catalog.ResponseDTO;

import java.util.List;

public class ProductResponseDTO {
    private Long productId;
    private String name;
    private Double basePrice;
    private Double totalPrice;
    private String previewImage;
    private boolean isActive; // --- NEW ADDITION ---
    private List<CustomOptionResponseDTO> customOptions;

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public List<CustomOptionResponseDTO> getCustomOptions() { return customOptions; }
    public void setCustomOptions(List<CustomOptionResponseDTO> customOptions) { this.customOptions = customOptions; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPreviewImage() { return previewImage; }
    public void setPreviewImage(String previewImage) { this.previewImage = previewImage; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}