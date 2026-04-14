package com.shopsphere.order.Service;

import com.shopsphere.order.DTO.OrderRequest;
import com.shopsphere.order.DTO.OrderResponse;
// import com.shopsphere.order.Entity.OrderEntity;
import com.shopsphere.order.Enums.OrderStatus;
// import com.shopsphere.order.Repository.OrderRepository;

import java.util.List;

public interface OrderService {

    public List<OrderResponse> getAllOrders();

    public OrderResponse getOrderById(Long orderId);

    public OrderResponse placeOrder(OrderRequest orderRequest);

    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus);

    public OrderResponse cancelOrder(Long orderId);

    public OrderResponse returnOrder(Long orderId);
}
