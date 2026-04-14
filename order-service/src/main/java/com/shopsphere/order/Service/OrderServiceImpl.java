package com.shopsphere.order.Service;

import com.shopsphere.order.DTO.*;
import com.shopsphere.order.Entity.OrderEntity;
import com.shopsphere.order.Enums.OrderStatus;
import com.shopsphere.order.Exception.ResourceNotFoundException;
import com.shopsphere.order.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserClient userClient;
    private final ProductClient productClient;
    private final LogisticsClient logisticsClient;
    private final OrderRepository orderRepository;

    private static final Map<OrderStatus, List<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.CONFIRMED, List.of(OrderStatus.PACKED, OrderStatus.CANCELLED),
            OrderStatus.PACKED, List.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, List.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, List.of(OrderStatus.RETURNED),
            OrderStatus.CANCELLED, List.of(),
            OrderStatus.RETURNED, List.of());

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order ID is not Found"));
        return mapToResponse(order);
    }

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest, String username) {
        log.info("Placing order for user: {}", username);

        ProductDTO product = productClient.findProductById(orderRequest.getProductId());
        UserDTO user = userClient.getUserByUserName(username);

        if (user == null) {
            throw new ResourceNotFoundException("Customer Not found");
        }
        if (product == null) {
            throw new ResourceNotFoundException("Product Not Found");
        }
        if (product.getTotalPrice() == null) {
            throw new IllegalStateException("Product price calculation failed in Catalog Service");
        }

        OrderEntity orders = new OrderEntity();
        orders.setProductId(product.getProductId());
        orders.setCustomerId(user.getId());

        double unitPrice = product.getTotalPrice();
        orders.setPriceAtPurchase(unitPrice);
        orders.setTotalAmount(unitPrice * orderRequest.getQuantity());
        orders.setStatus(OrderStatus.CONFIRMED);
        orders.setQuantity(orderRequest.getQuantity());

        OrderEntity savedOrder = orderRepository.save(orders);
        log.info("Order {} successfully placed by user: {}", savedOrder.getOrderId(), username);
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));

        ValidTransaction(order.getStatus(), newStatus);
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        OrderEntity savedOrder = orderRepository.save(order);

        if (newStatus == OrderStatus.SHIPPED) {
            try {
                logisticsClient.createShipment(String.valueOf(orderId));
                log.info("Shipment created successfully for Order {}", orderId);
            } catch (Exception e) {
                log.error("Could not create shipment for Order {}: {}", orderId, e.getMessage());
            }
        }

        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));

        ValidTransaction(order.getStatus(), OrderStatus.CANCELLED);
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        log.info("Order {} cancelled", orderId);
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse returnOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));

        ValidTransaction(order.getStatus(), OrderStatus.RETURNED);
        order.setStatus(OrderStatus.RETURNED);
        order.setUpdatedAt(LocalDateTime.now());

        log.info("Order {} returned", orderId);
        return mapToResponse(orderRepository.save(order));
    }

    private void ValidTransaction(OrderStatus current, OrderStatus newStatus) {
        List<OrderStatus> valid = ALLOWED_TRANSITIONS.getOrDefault(current, List.of());
        if (!valid.contains(newStatus)) {
            throw new IllegalStateException(
                    "Invalid transition: " + current + " → " + newStatus + ". Allowed: " + valid);
        }
    }

    private OrderResponse mapToResponse(OrderEntity orderEntity) {
        OrderResponse res = new OrderResponse();
        res.setOrderId(orderEntity.getOrderId());
        res.setCustomerId(orderEntity.getCustomerId());
        res.setProductId(orderEntity.getProductId());
        res.setOrderStatus(orderEntity.getStatus());
        res.setUnitPriceAtPurchase(orderEntity.getPriceAtPurchase());
        res.setTotalOrderAmount(orderEntity.getTotalAmount());
        res.setCreatedAt(orderEntity.getCreatedAt());
        res.setUpdatedAt(orderEntity.getUpdatedAt());

        if (orderEntity.getStatus() == OrderStatus.SHIPPED || orderEntity.getStatus() == OrderStatus.DELIVERED) {
            try {
                ShipmentResponse shipment = logisticsClient.getByOrderId(String.valueOf(orderEntity.getOrderId()));
                if (shipment != null) {
                    res.setTrackingUrl(shipment.getTrackingUrl());
                    res.setCarrier(shipment.getCarrier());
                }
            } catch (Exception e) {
                res.setTrackingUrl("Tracking information currently unavailable");
            }
        }
        return res;
    }
}