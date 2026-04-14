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

        if (user == null || product == null || product.getTotalPrice() == null) {
            throw new ResourceNotFoundException("Validation failed for User or Product.");
        }

        StockRequest stockRequest = new StockRequest(String.valueOf(product.getProductId()), orderRequest.getQuantity());
        try {
            Boolean isStockAvailable = inventoryClient.checkStock(stockRequest).getBody();
            if (Boolean.FALSE.equals(isStockAvailable)) {
                throw new IllegalStateException("Insufficient stock available for this product.");
            }
            inventoryClient.deductStock(stockRequest);
        } catch (Exception e) {
            throw new IllegalStateException("Could not verify or deduct inventory at this time.");
        }

        OrderEntity order = new OrderEntity();
        order.setProductId(product.getProductId());
        order.setCustomerId(user.getId());
        order.setCustomizationDetails(orderRequest.getCustomizationDetails());

        double unitPrice = product.getTotalPrice();
        order.setPriceAtPurchase(unitPrice);
        order.setTotalAmount(unitPrice * orderRequest.getQuantity());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setQuantity(orderRequest.getQuantity());
        order.setPaymentStatus(PaymentStatus.PENDING); // Initial status

        OrderEntity savedOrder = orderRepository.save(order);
        log.info("Order successfully placed. Awaiting Payment for Order ID: {}", savedOrder.getOrderId());
        
        return mapToResponse(savedOrder);
    }

    // --- LLD REQUIRED: Payment Processing Mock ---
    @Override
    public OrderResponse processPayment(Long orderId, PaymentRequest request) {
        log.info("Processing {} payment for Order ID: {}", request.getPaymentMethod(), orderId);
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));

        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Payment has already been completed for this order.");
        }

        // Mocking communication with Stripe/Razorpay
        String mockTransactionId = "txn_" + request.getPaymentMethod().toLowerCase() + "_" + UUID.randomUUID().toString().substring(0, 8);
        
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setTransactionId(mockTransactionId);
        order.setUpdatedAt(LocalDateTime.now());

        log.info("Payment Successful. Transaction ID: {}", mockTransactionId);
        return mapToResponse(orderRepository.save(order));
    }
    // ---------------------------------------------

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
        
        // Auto-refund logic
        if(order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            log.info("Order Cancelled. Initiating refund for Transaction: {}", order.getTransactionId());
        }
        
        order.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse returnOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));
        ValidTransaction(order.getStatus(), OrderStatus.RETURNED);
        order.setStatus(OrderStatus.RETURNED);
        
        // Auto-refund logic
        if(order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            log.info("Order Returned. Initiating refund for Transaction: {}", order.getTransactionId());
        }

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
            throw new IllegalStateException("Invalid transition: " + current + " → " + newStatus);
        }
    }

    private OrderResponse mapToResponse(OrderEntity orderEntity) {
        OrderResponse res = new OrderResponse();
        res.setOrderId(orderEntity.getOrderId());
        res.setCustomerId(orderEntity.getCustomerId());
        res.setProductId(orderEntity.getProductId());
        res.setCustomizationDetails(orderEntity.getCustomizationDetails()); 
        res.setOrderStatus(orderEntity.getStatus());
        res.setPaymentStatus(orderEntity.getPaymentStatus()); // Mapping payment
        res.setTransactionId(orderEntity.getTransactionId()); // Mapping transaction
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