package com.shopsphere.order.Controller;

import com.shopsphere.order.DTO.OrderRequest;
import com.shopsphere.order.DTO.OrderResponse;
import com.shopsphere.order.DTO.StatusUpdateRequest;
import com.shopsphere.order.DTO.UserClient;
import com.shopsphere.order.DTO.UserDTO;
import com.shopsphere.order.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    
    // FIX: Inject UserClient to securely map the Header Email to the Numeric ID
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

    @PostMapping("/{orderId}/confirm-payment")
    public ResponseEntity<OrderResponse> confirmPayment(@PathVariable Long orderId) {
        log.info("Payment confirmed for Order ID: {}", orderId);
        OrderResponse response = orderService.confirmPayment(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // Retained for Logistics/Admin fetching
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    // ==========================================
    // 🛡️ THE SECURE FRONTEND ENDPOINT FIX 🛡️
    // ==========================================
    @GetMapping("/my-history")
    public ResponseEntity<List<OrderResponse>> getMySecureOrderHistory(
            @RequestHeader(value = "X-Logged-In-User") String username) {
        
        log.info("Fetching secure order history for: {}", username);
        // Look up the database ID internally, so the frontend never has to guess it!
        UserDTO user = userClient.getUserByUserName(username);
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(user.getId()));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, request.getNewStatus()));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @PutMapping("/{orderId}/return")
    public ResponseEntity<OrderResponse> returnOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.returnOrder(orderId));
    }
}