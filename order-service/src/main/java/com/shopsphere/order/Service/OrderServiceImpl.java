package com.shopsphere.order.Service;

import com.shopsphere.order.DTO.*;
import com.shopsphere.order.Entity.OrderEntity;
import com.shopsphere.order.Enums.OrderStatus;
import com.shopsphere.order.Enums.PaymentStatus;
import com.shopsphere.order.Exception.ResourceNotFoundException;
import com.shopsphere.order.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final UserClient userClient;
    private final ProductClient productClient;
    private final LogisticsClient logisticsClient;
    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;

    @Override
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders from the database");
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        log.info("Fetching order details for ID: {}", orderId);
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order ID " + orderId + " not found"));
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        log.info("Initiating order placement for user: {}", orderRequest.getUserName());

        ProductDTO product = productClient.findProductById(orderRequest.getProductId());
        UserDTO user = userClient.getUserByUserName(orderRequest.getUserName());

        if (user == null || product == null || product.getTotalPrice() == null) {
            throw new ResourceNotFoundException("Validation failed: User or Product not found.");
        }

        // --- 1. MOCK PAYMENT GATEWAY ---
        log.info("Processing mock payment step...");
        if (orderRequest.getPaymentMode() != null && orderRequest.getPaymentMode().equalsIgnoreCase("FAIL_TEST")) {
            log.error("Payment gateway declined transaction for user: {}", orderRequest.getUserName());
            throw new IllegalStateException("Payment failed: Transaction declined by gateway.");
        }

        // --- 2. STOCK VALIDATION & DEDUCTION ---
        StockRequest stockRequest = new StockRequest(String.valueOf(product.getProductId()), orderRequest.getQuantity());
        Boolean isStockAvailable = inventoryClient.checkStock(stockRequest).getBody();

        if (Boolean.FALSE.equals(isStockAvailable)) {
            log.warn("Stock check failed for product: {}", product.getProductId());
            throw new IllegalStateException("Insufficient stock available.");
        }

        inventoryClient.deductStock(stockRequest);
        log.info("Stock successfully reserved for product: {}", product.getProductId());

        // --- 3. PERSIST ORDER WITH COMPENSATING TRANSACTION ---
        OrderEntity order = new OrderEntity();
        order.setProductId(product.getProductId());
        order.setCustomerId(user.getId());
        order.setPriceAtPurchase(product.getTotalPrice());
        order.setTotalAmount(product.getTotalPrice() * orderRequest.getQuantity());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setQuantity(orderRequest.getQuantity());
        order.setPaymentStatus(PaymentStatus.COMPLETED); // Assuming instant mock success
        order.setTransactionId("txn_" + UUID.randomUUID().toString().substring(0, 8));

        try {
            OrderEntity savedOrder = orderRepository.save(order);
            log.info("Order {} saved successfully", savedOrder.getOrderId());
            return mapToResponse(savedOrder);
        } catch (Exception e) {
            // --- 4. THE ROLLBACK (PUTTING STOCK BACK) ---
            log.error("Database save failed. Rolling back inventory for product: {}", product.getProductId());
            inventoryClient.refundStock(stockRequest); 
            throw new IllegalStateException("Internal system error: Order could not be saved. Inventory has been released.", e);
        }
    }

    @Override
    public OrderResponse processPayment(Long orderId, PaymentRequest request) {
        log.info("Processing manual payment update for Order ID: {}", orderId);
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Order is already paid.");
        }

        order.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setTransactionId("txn_" + UUID.randomUUID().toString().substring(0, 8));
        order.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating status for Order ID: {} to {}", orderId, newStatus);
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        ValidTransaction(order.getStatus(), newStatus);
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        OrderEntity savedOrder = orderRepository.save(order);

        if (newStatus == OrderStatus.SHIPPED) {
            try {
                logisticsClient.createShipment(String.valueOf(orderId));
            } catch (Exception e) {
                log.error("Logistics integration failed for Order {}: {}", orderId, e.getMessage());
            }
        }
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        log.info("Cancelling Order ID: {}", orderId);
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        ValidTransaction(order.getStatus(), OrderStatus.CANCELLED);
        order.setStatus(OrderStatus.CANCELLED);
        
        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            log.info("Order cancelled. Payment marked for refund.");
        }
        
        order.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse returnOrder(Long orderId) {
        log.info("Initiating return for Order ID: {}", orderId);
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        ValidTransaction(order.getStatus(), OrderStatus.RETURNED);
        order.setStatus(OrderStatus.RETURNED);
        
        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        order.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(orderRepository.save(order));
    }

    private void ValidTransaction(OrderStatus current, OrderStatus newStatus) {
        List<OrderStatus> validTransitions = ALLOWED_TRANSITIONS.getOrDefault(current, List.of());
        if (!validTransitions.contains(newStatus)) {
            throw new IllegalStateException("Transition from " + current + " to " + newStatus + " is not allowed.");
        }
    }

    private static final Map<OrderStatus, List<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.CONFIRMED, List.of(OrderStatus.PACKED, OrderStatus.CANCELLED),
            OrderStatus.PACKED, List.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, List.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, List.of(OrderStatus.RETURNED),
            OrderStatus.CANCELLED, List.of(),
            OrderStatus.RETURNED, List.of()
    );

    private OrderResponse mapToResponse(OrderEntity entity) {
        OrderResponse res = new OrderResponse();
        res.setOrderId(entity.getOrderId());
        res.setCustomerId(entity.getCustomerId());
        res.setProductId(entity.getProductId());
        res.setOrderStatus(entity.getStatus());
        res.setPaymentStatus(entity.getPaymentStatus());
        res.setTransactionId(entity.getTransactionId());
        res.setUnitPriceAtPurchase(entity.getPriceAtPurchase());
        res.setTotalOrderAmount(entity.getTotalAmount());
        res.setCreatedAt(entity.getCreatedAt());
        res.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getStatus() == OrderStatus.SHIPPED || entity.getStatus() == OrderStatus.DELIVERED) {
            try {
                ShipmentResponse shipment = logisticsClient.getByOrderId(String.valueOf(entity.getOrderId()));
                if (shipment != null) {
                    res.setTrackingUrl(shipment.getTrackingUrl());
                    res.setCarrier(shipment.getCarrier());
                }
            } catch (Exception e) {
                res.setTrackingUrl("Tracking information unavailable");
            }
        }
        return res;
    }
}