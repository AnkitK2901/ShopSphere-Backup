package com.shopsphere.order.Service;

import com.shopsphere.order.DTO.*;
import com.shopsphere.order.Entity.CartEntity;
import com.shopsphere.order.Entity.OrderEntity;
import com.shopsphere.order.Entity.OrderItemEntity;
import com.shopsphere.order.Enums.OrderStatus;
import com.shopsphere.order.Exception.ResourceNotFoundException;
import com.shopsphere.order.Repository.CartRepository;
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

    @Autowired
    private UserClient userClient;
    @Autowired
    private ProductClient productClient;
    @Autowired
    private LogisticsClient logisticsClient;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private InventoryClient inventoryClient;
    @Autowired
    private AnalyticsClient analyticsClient;
    
    @Autowired
    private CartRepository cartRepository; 

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
        return orderRepository.findByCustomerId(customerId).stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        UserDTO user = userClient.getUserByUserName(orderRequest.getUserName());
        if (user == null) {
            throw new ResourceNotFoundException("Customer Not found");
        }

        double totalOrderAmount = 0.0;
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : orderRequest.getItems()) {
            ProductDTO product = productClient.findProductById(itemReq.getProductId());
            if (product == null)
                throw new ResourceNotFoundException("Product Not Found: " + itemReq.getProductId());

            Boolean inStock = inventoryClient.checkStock(new StockRequest(product.getProductId(), itemReq.getQuantity())).getBody();
            if (inStock != null && !inStock) {
                throw new IllegalStateException("Insufficient stock to fulfill order for product: " + product.getName());
            }

            double unitPrice = product.getTotalPrice() > 0.0 ? product.getTotalPrice() : product.getBasePrice();
            
            if (unitPrice <= 0.0) throw new ResourceNotFoundException("Product price is completely unavailable");

            // 🛡️ THE 'None' FIX: We only add price if they picked a REAL option, not 'None'
            if (itemReq.getSelectedOption() != null && !itemReq.getSelectedOption().isEmpty() && !itemReq.getSelectedOption().equals("None")) {
                if (product.getCustomOptions() != null) {
                    for (CustomOptionDTO opt : product.getCustomOptions()) {
                        String optionMatch = opt.getType() + ": " + opt.getValue();
                        if (optionMatch.equals(itemReq.getSelectedOption())) {
                            unitPrice += opt.getPriceAdjustment(); 
                            break;
                        }
                    }
                }
            }

            double itemTotal = unitPrice * itemReq.getQuantity();
            totalOrderAmount += itemTotal;

            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setProductId(product.getProductId());
            itemEntity.setQuantity(itemReq.getQuantity());
            itemEntity.setPrice(unitPrice);
            itemEntity.setSelectedOption(itemReq.getSelectedOption());
            orderItems.add(itemEntity);
        }

        if (orderRequest.getExpectedTotal() != null) {
            if (Math.abs(totalOrderAmount - orderRequest.getExpectedTotal()) > 0.01) {
                throw new IllegalStateException("Security Alert: Price Mismatch Detected.");
            }
        }

        OrderEntity order = new OrderEntity();
        order.setCustomerId(user.getId());
        order.setTotalAmount(totalOrderAmount);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setItems(orderItems);
        order.setShippingAddress(orderRequest.getShippingAddress());

        cartRepository.findById(user.getId()).ifPresent(cartRepository::delete);

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
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));
        
        // 🛡️ THE IDEMPOTENCY FIX: Silently ignore duplicate status updates!
        if (order.getStatus() == newStatus) {
            return mapToResponse(order);
        }

        ValidTransaction(order.getStatus(), newStatus);

        if (newStatus == OrderStatus.SHIPPED) {
            try {
                logisticsClient.createShipment(orderId);
            } catch (Exception e) {
                throw new IllegalStateException("Logistics failure.", e);
            }
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));
        ValidTransaction(order.getStatus(), OrderStatus.CANCELLED);
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse returnOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Id not found"));
        ValidTransaction(order.getStatus(), OrderStatus.RETURNED);
        order.setStatus(OrderStatus.RETURNED);
        order.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void syncCart(String username, String cartJson) {
        try {
            UserDTO user = userClient.getUserByUserName(username);
            if (user != null) {
                CartEntity cart = cartRepository.findById(user.getId()).orElse(new CartEntity());
                cart.setCustomerId(user.getId());
                cart.setCartJson(cartJson);
                cart.setLastUpdated(LocalDateTime.now());
                cartRepository.save(cart);
            }
        } catch (Exception e) {
            System.err.println("Silent cart sync failed for user: " + username);
        }
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
        res.setShippingAddress(orderEntity.getShippingAddress());

        if (orderEntity.getItems() != null && !orderEntity.getItems().isEmpty()) {
            List<OrderItemEntity> distinctItems = new ArrayList<>();
            for (OrderItemEntity item : orderEntity.getItems()) {
                boolean exists = distinctItems.stream().anyMatch(d -> 
                    d.getProductId().equals(item.getProductId()) && 
                    // 🛡️ THE 'None' FIX: Safe check for string matching
                    (d.getSelectedOption() == null ? item.getSelectedOption() == null : d.getSelectedOption().equals(item.getSelectedOption()))
                );
                if (!exists) {
                    distinctItems.add(item);
                }
            }

            List<OrderItemResponse> mappedItems = distinctItems.stream().map(item -> {
                OrderItemResponse ir = new OrderItemResponse();
                ir.setProductId(item.getProductId());
                ir.setQuantity(item.getQuantity());
                ir.setPrice(item.getPrice());
                ir.setSelectedOption(item.getSelectedOption());
                return ir;
            }).collect(Collectors.toList());
            res.setItems(mappedItems);

            res.setProductId(distinctItems.get(0).getProductId());
            res.setQuantity(distinctItems.get(0).getQuantity());
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