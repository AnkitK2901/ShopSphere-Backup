package com.shopsphere.order.Controller;

import com.shopsphere.order.DTO.OrderRequest;
import com.shopsphere.order.DTO.OrderResponse;
import com.shopsphere.order.DTO.StatusUpdateRequest;
import com.shopsphere.order.DTO.UserClient;
import com.shopsphere.order.DTO.UserDTO;
import com.shopsphere.order.Enums.OrderStatus;
import com.shopsphere.order.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserClient userClient; 

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            @RequestHeader(value = "X-Logged-In-User", defaultValue = "UNKNOWN_USER") String username) {

        log.info("Order placement initiated by secure user: {}", username);
        orderRequest.setUserName(username);
        OrderResponse response = orderService.placeOrder(orderRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // THE FIX: Explicitly defined name inside @PathVariable
    @PostMapping("/{orderId}/confirm-payment")
    public ResponseEntity<OrderResponse> confirmPayment(@PathVariable("orderId") Long orderId) {
        log.info("Payment confirmed for Order ID: {}", orderId);
        OrderResponse response = orderService.confirmPayment(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // THE FIX: Explicitly defined name inside @PathVariable
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // THE FIX: Explicitly defined name inside @PathVariable
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(@PathVariable("customerId") Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<OrderResponse>> getMySecureOrderHistory(
            @RequestHeader(value = "X-Logged-In-User") String username) {
        
        log.info("Fetching secure order history for: {}", username);
        UserDTO user = userClient.getUserByUserName(username);
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(user.getId()));
    }

    // THE FIX: Explicitly defined name inside @PathVariable
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable("orderId") Long orderId,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, request.getNewStatus()));
    }

    // THE FIX: Explicitly defined name inside @PathVariable
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    // THE FIX: Explicitly defined name inside @PathVariable
    @PutMapping("/{orderId}/return")
    public ResponseEntity<OrderResponse> returnOrder(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(orderService.returnOrder(orderId));
    }

    // ==========================================
    // 🛡️ THE ABANDONED CART FIX 🛡️
    // ==========================================
    @PostMapping("/cart/sync")
    public ResponseEntity<Void> syncCart(
            @RequestBody String cartJson, 
            @RequestHeader(value = "X-Logged-In-User") String username) {
        orderService.syncCart(username, cartJson);
        return ResponseEntity.ok().build();
    }

    // THE FIX: Endpoint for Logistics Sweeper
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable("status") OrderStatus status) {
        List<OrderResponse> allOrders = orderService.getAllOrders();
        List<OrderResponse> filtered = allOrders.stream()
                .filter(o -> o.getOrderStatus() == status)
                .collect(Collectors.toList());
        return ResponseEntity.ok(filtered);
    }
}