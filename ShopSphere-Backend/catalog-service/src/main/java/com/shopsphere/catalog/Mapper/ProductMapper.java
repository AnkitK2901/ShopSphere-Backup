package com.shopsphere.catalog.Mapper;

import com.shopsphere.catalog.Entity.Product;
import com.shopsphere.catalog.RequestDTO.ProductRequestDTO;
import com.shopsphere.catalog.ResponseDTO.CustomOptionResponseDTO;
import com.shopsphere.catalog.ResponseDTO.ProductResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    // RESTORED METHOD NAME TO EXACTLY toDTO
    public static ProductResponseDTO toDTO(Product product) {
        List<CustomOptionResponseDTO> optionDTOs = null;
        if (product.getCustomOptions() != null) {
            optionDTOs = product.getCustomOptions().stream()
                    .map(opt -> {
                        // Safe object creation to prevent constructor mismatch errors
                        CustomOptionResponseDTO dto = new CustomOptionResponseDTO();
                        dto.setId(opt.getId());
                        dto.setType(opt.getType());
                        dto.setValue(opt.getValue());
                        dto.setPriceAdjustment(opt.getPriceAdjustment());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription()); // safely added
        dto.setBasePrice(product.getBasePrice());
        dto.setPreviewImage(product.getPreviewImage());
        dto.setActive(product.isActive());
        dto.setCustomOptions(optionDTOs);
        
        return dto;
    }

    public static Product toEntity(ProductRequestDTO requestDTO) {
        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription()); // safely added
        product.setBasePrice(requestDTO.getBasePrice());
        product.setPreviewImage(requestDTO.getPreviewImage());
        product.setActive(requestDTO.isActive());
        return product;
    }
}