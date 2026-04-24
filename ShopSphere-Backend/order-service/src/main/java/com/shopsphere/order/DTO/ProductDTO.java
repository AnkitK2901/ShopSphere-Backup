package com.shopsphere.order.DTO;

import java.util.ArrayList;
import java.util.List;

public class ProductDTO {
    private String productId;
    private String name;
    private Double basePrice;
    private String previewImage;
    private Double totalPrice;
    private List<CustomOptionDTO> customOptions = new ArrayList<>();

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public List<CustomOptionDTO> getCustomOptions() {
        return customOptions;
    }

    public void setCustomOptions(List<CustomOptionDTO> customOptions) {
        this.customOptions = customOptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
