package com.shopsphere.order.Service;

import com.shopsphere.order.DTO.*;
import com.shopsphere.order.Entity.OrderEntity;
import com.shopsphere.order.Entity.OrderItemEntity;
import com.shopsphere.order.Enums.OrderStatus;
import com.shopsphere.order.Exception.ResourceNotFoundException;
import com.shopsphere.order.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private UserClient userClient;
    @Autowired private ProductClient productClient;
    @Autowired private LogisticsClient logisticsClient;
    @Autowired private OrderRepository orderRepository;
    @Autowired private InventoryClient inventoryClient;
    @Autowired private AnalyticsClient analyticsClient; 

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order ID is not Found"));
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        UserDTO user = userClient.getUserByUserName(orderRequest.getUserName());
        if (user == null) { throw new ResourceNotFoundException("Customer Not found"); }

        double totalOrderAmount = 0.0;
        List<OrderItemEntity> orderItems = new ArrayList<>();
        List<String> allCustomizations = new ArrayList<>(); // To store everyone's choices

        for (OrderItemRequest itemReq : orderRequest.getItems()) {
            ProductDTO product = productClient.findProductById(itemReq.getProductId());
            if (product == null) throw new ResourceNotFoundException("Product Not Found: " + itemReq.getProductId());

            Double unitPrice = product.getTotalPrice() != null ? product.getTotalPrice() : product.getBasePrice();
            if (unitPrice == null) throw new ResourceNotFoundException("Product price is completely unavailable");

            // Collect product-specific customizations
            if (product.getCustomOptions() != null) {
                for (CustomOptionDTO opt : product.getCustomOptions()) {
                    allCustomizations.add(product.getName() + " [" + opt.getType() + ": " + opt.getValue() + "]");
                }
            }

            double itemTotal = unitPrice * itemReq.getQuantity();
            totalOrderAmount += itemTotal;

            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setProductId(product.getProductId());
            itemEntity.setQuantity(itemReq.getQuantity());
            itemEntity.setPrice(unitPrice);
            orderItems.add(itemEntity);
        }

        OrderEntity order = new OrderEntity();
        order.setCustomerId(user.getId());
        order.setTotalAmount(totalOrderAmount);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setItems(orderItems); 
        order.setCustomizationDetails(allCustomizations); // FIX: Now successfully calling the setter

        return mapToResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(rollbackFor = Exception.class) 
    public OrderResponse confirmPayment(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));

        ValidTransaction(order.getStatus(), OrderStatus.CONFIRMED);

        List<StockRequest> deductedStocks = new ArrayList<>();

        try {
            for (OrderItemEntity item : order.getItems()) {
                StockRequest stockRequest = new StockRequest(item.getProductId(), item.getQuantity());
                inventoryClient.deductStock(stockRequest);
                deductedStocks.add(stockRequest); 
            }

            order.setStatus(OrderStatus.CONFIRMED);
            order.setUpdatedAt(LocalDateTime.now());
            OrderEntity savedOrder = orderRepository.save(order);

            try {
                analyticsClient.logRevenueEvent(savedOrder.getTotalAmount());
            } catch (Exception analyticsErr) {
                System.err.println("WARNING: Analytics tracking failed for Order " + orderId);
            }

            return mapToResponse(savedOrder);

        } catch (Exception e) {
            for (StockRequest refundReq : deductedStocks) {
                try {
                    inventoryClient.refundStock(refundReq);
                } catch (Exception refundException) {
                    System.err.println("CRITICAL FAILURE: Stock refund failed for product " + refundReq.getProductId());
                }
            }
            throw new IllegalStateException("Payment confirmation failed. Transaction rolled back.", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class) 
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));
        ValidTransaction(order.getStatus(), newStatus);

        if (newStatus == OrderStatus.SHIPPED) {
            try { logisticsClient.createShipment(orderId); } 
            catch (Exception e) { throw new IllegalStateException("Logistics failure.", e); }
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));
        ValidTransaction(order.getStatus(), OrderStatus.CANCELLED);
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse returnOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));
        ValidTransaction(order.getStatus(), OrderStatus.RETURNED);
        order.setStatus(OrderStatus.RETURNED);
        order.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(orderRepository.save(order));
    }

    private static final Map<OrderStatus, List<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING_PAYMENT, List.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, List.of(OrderStatus.PACKED, OrderStatus.CANCELLED),
            OrderStatus.PACKED, List.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, List.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, List.of(OrderStatus.RETURNED),
            OrderStatus.CANCELLED, List.of(),
            OrderStatus.RETURNED, List.of());

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
        res.setOrderStatus(orderEntity.getStatus());
        res.setTotalOrderAmount(orderEntity.getTotalAmount());
        res.setCreatedAt(orderEntity.getCreatedAt());
        res.setUpdatedAt(orderEntity.getUpdatedAt());
        res.setCustomizationDetails(orderEntity.getCustomizationDetails());

        if (orderEntity.getItems() != null && !orderEntity.getItems().isEmpty()) {
            List<OrderItemResponse> mappedItems = orderEntity.getItems().stream().map(item -> {
                OrderItemResponse ir = new OrderItemResponse();
                ir.setProductId(item.getProductId());
                ir.setQuantity(item.getQuantity());
                ir.setPrice(item.getPrice());
                return ir;
            }).collect(Collectors.toList());
            res.setItems(mappedItems);
            
            res.setProductId(orderEntity.getItems().get(0).getProductId());
            res.setQuantity(orderEntity.getItems().get(0).getQuantity());
        }

        if (orderEntity.getStatus() == OrderStatus.SHIPPED || orderEntity.getStatus() == OrderStatus.DELIVERED) {
            try {
                ShipmentResponse shipment = logisticsClient.getByOrderId(orderEntity.getOrderId());
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