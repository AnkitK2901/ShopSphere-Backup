package com.shopsphere.catalog.Mapper;

import com.shopsphere.catalog.Entity.CustomOption;
import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.ResponseDTO.CustomOptionResponseDTO;
import com.shopsphere.catalog.ResponseDTO.ProductResponseDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductResponseDTO toDTO(Product product) {
        if (product == null) return null;

        ProductResponseDTO dto = new ProductResponseDTO();

        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setBasePrice(product.getBasePrice());
        dto.setPreviewImage(product.getPreviewImage());

        product.calculateTotalPrice();
        dto.setTotalPrice(product.getTotalPrice());

        if (product.getCustomOptions() != null) {
            List<CustomOptionResponseDTO> optionDTOs = product.getCustomOptions()
                    .stream()
                    .map(ProductMapper::toOptionDTO)
                    .collect(Collectors.toList());
            dto.setCustomOptions(optionDTOs);
        } else {
            dto.setCustomOptions(Collections.emptyList());
        }

        return dto;
    }

    public static CustomOptionResponseDTO toOptionDTO(CustomOption option) {
        if (option == null) return null;

        CustomOptionResponseDTO dto = new CustomOptionResponseDTO();
        dto.setId(option.getId());
        dto.setType(option.getType());
        dto.setValue(option.getValue());
        dto.setPriceAdjustment(option.getPriceAdjustment());

        return dto;
    }
}