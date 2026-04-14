package com.shopsphere.catalog.ResponseDTO;

import lombok.Data;

@Data // <--- This generates the missing setters and getters!
public class CustomOptionResponseDTO {
    private Long optionId;
    private String optionName;
    private String type;
    private Double priceModifier;
}