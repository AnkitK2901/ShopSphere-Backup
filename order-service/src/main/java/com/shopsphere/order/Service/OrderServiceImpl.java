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
@RequiredArgsConstructor
@Slf4j // Replaced System prints with proper logging
public class OrderServiceImpl implements OrderService {

    private final UserClient userClient;
    private final ProductClient productClient;
    private final LogisticsClient logisticsClient;
    private final InventoryClient inventoryClient; // Added Inventory Client
    private final OrderRepository orderRepository;

    @Override
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        log.info("Fetching order with ID: {}", orderId);
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order ID " + orderId + " is not Found"));
        return mapToResponse(order);
    }

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        log.info("Initiating order placement for user: {} and product: {}", orderRequest.getUserName(), orderRequest.getProductId());

        ProductDTO product = productClient.findProductById(orderRequest.getProductId());
        UserDTO user = userClient.getUserByUserName(orderRequest.getUserName());

        if (user == null) {
            log.error("Customer not found: {}", orderRequest.getUserName());
            throw new ResourceNotFoundException("Customer Not found");
        }

        if (product == null) {
            log.error("Product not found: {}", orderRequest.getProductId());
            throw new ResourceNotFoundException("Product Not Found");
        }

        if (product.getTotalPrice() == null) {
            log.error("Product price calculation failed for product: {}", product.getProductId());
            throw new IllegalStateException("Product price calculation failed in Catalog Service");
        }

        // --- NEW: INVENTORY CHECK AND DEDUCTION ---
        StockRequest stockRequest = new StockRequest(String.valueOf(product.getProductId()), orderRequest.getQuantity());
        
        try {
            Boolean isStockAvailable = inventoryClient.checkStock(stockRequest).getBody();
            if (Boolean.FALSE.equals(isStockAvailable)) {
                log.warn("Insufficient stock for product ID: {}", product.getProductId());
                throw new IllegalStateException("Insufficient stock available for this product.");
            }
            // If available, deduct the stock
            inventoryClient.deductStock(stockRequest);
            log.info("Successfully deducted {} units for product {}", orderRequest.getQuantity(), product.getProductId());
        } catch (Exception e) {
            log.error("Failed to communicate with Inventory Service: {}", e.getMessage());
            throw new IllegalStateException("Could not verify or deduct inventory at this time. Please try again.");
        }
        // ------------------------------------------

        OrderEntity order = new OrderEntity();
        order.setProductId(product.getProductId());
        order.setCustomerId(user.getId());
        order.setCustomizationDetails(orderRequest.getCustomizationDetails()); // Save custom options

        double unitPrice = product.getTotalPrice();
        order.setPriceAtPurchase(unitPrice);
        order.setTotalAmount(unitPrice * orderRequest.getQuantity());

        order.setStatus(OrderStatus.CONFIRMED);
        order.setQuantity(orderRequest.getQuantity());

        OrderEntity savedOrder = orderRepository.save(order);
        log.info("Order successfully placed. Order ID: {}", savedOrder.getOrderId());
        
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating status for Order ID: {} to {}", orderId, newStatus);
        
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));

        ValidTransaction(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        OrderEntity savedOrder = orderRepository.save(order);

        if (newStatus == OrderStatus.SHIPPED) {
            try {
                logisticsClient.createShipment(String.valueOf(orderId));
                log.info("Shipment created successfully for Order ID: {}", orderId);
            } catch (Exception e) {
                log.error("Could not create shipment for Order {}: {}", orderId, e.getMessage());
            }
        }

        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        log.info("Cancelling order ID: {}", orderId);
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));

        ValidTransaction(order.getStatus(), OrderStatus.CANCELLED);

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse returnOrder(Long orderId) {
        log.info("Initiating return for order ID: {}", orderId);
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));

        ValidTransaction(order.getStatus(), OrderStatus.RETURNED);

        order.setStatus(OrderStatus.RETURNED);
        order.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(orderRepository.save(order));
    }

    private static final Map<OrderStatus, List<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.CONFIRMED, List.of(OrderStatus.PACKED, OrderStatus.CANCELLED),
            OrderStatus.PACKED, List.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, List.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, List.of(OrderStatus.RETURNED),
            OrderStatus.CANCELLED, List.of(),
            OrderStatus.RETURNED, List.of()
    );

    private void ValidTransaction(OrderStatus current, OrderStatus newStatus) {
        List<OrderStatus> valid = ALLOWED_TRANSITIONS.getOrDefault(current, List.of());
        if (!valid.contains(newStatus)) {
            log.warn("Invalid status transition attempted: {} to {}", current, newStatus);
            throw new IllegalStateException(
                    "Invalid transition: " + current + " → " + newStatus +
                            ". Allowed: " + valid
            );
        }
    }

    private OrderResponse mapToResponse(OrderEntity orderEntity) {
        OrderResponse res = new OrderResponse();
        res.setOrderId(orderEntity.getOrderId());
        res.setCustomerId(orderEntity.getCustomerId());
        res.setProductId(orderEntity.getProductId());
        res.setCustomizationDetails(orderEntity.getCustomizationDetails()); // Map field to response
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
                log.warn("Tracking information unavailable for Order ID {}: {}", orderEntity.getOrderId(), e.getMessage());
                res.setTrackingUrl("Tracking information currently unavailable");
            }
        }
        return res;
    }
}