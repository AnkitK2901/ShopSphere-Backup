package com.shopsphere.order.DTO;

public class OrderItemRequest {
    private Long productId; // THE FIX: Aligned with Inventory and Catalog
    private int quantity;
    private String selectedOption; 

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
}