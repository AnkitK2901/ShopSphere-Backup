package com.shopsphere.catalog.Mapper;

import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.Entity.CustomOption;
import com.shopsphere.catalog.ResponseDTO.ProductResponseDTO;
import com.shopsphere.catalog.ResponseDTO.CustomOptionResponseDTO;

import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductResponseDTO toDTO(Product product) {
        if (product == null) return null;
        
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setBasePrice(product.getBasePrice());
        dto.setTotalPrice(product.getTotalPrice());
        dto.setPreviewImage(product.getPreviewImage());
        
        // --- Mapping the new fields ---
        dto.setVendorId(product.getVendorId()); 
        dto.setRegion(product.getRegion());     
        // ------------------------------

        if (product.getCustomOptions() != null) {
            dto.setCustomOptions(product.getCustomOptions().stream()
                    .map(ProductMapper::toOptionDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public static CustomOptionResponseDTO toOptionDTO(CustomOption option) {
        if (option == null) return null;
        CustomOptionResponseDTO dto = new CustomOptionResponseDTO();
        dto.setOptionId(option.getOptionId());
        dto.setOptionName(option.getOptionName());
        dto.setPriceModifier(option.getPriceModifier());
        return dto;
    }
}