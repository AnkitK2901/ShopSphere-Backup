package com.shopsphere.order.DTO;

public class OrderItemResponse {
    private String productId;
    private int quantity;
    private Double price;

    public OrderItemResponse() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}