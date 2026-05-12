package com.shopsphere.order.DTO;

public class OrderItemRequest {
    private String productId;
    private int quantity;
    private String selectedOption; // THE FIX: Capture the specific choice

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
}