package com.shopsphere.order.Service;

import com.shopsphere.order.DTO.OrderRequest;
import com.shopsphere.order.DTO.OrderResponse;
import com.shopsphere.order.Enums.OrderStatus;

import java.util.List;

public interface OrderService {

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(Long orderId);

    List<OrderResponse> getOrdersByCustomerId(Long customerId);

    OrderResponse placeOrder(OrderRequest orderRequest);

    OrderResponse confirmPayment(Long orderId);

    OrderResponse updateStatus(Long orderId, OrderStatus newStatus);

    OrderResponse cancelOrder(Long orderId);

    OrderResponse returnOrder(Long orderId);

    void syncCart(String username, String cartJson);
}