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
import java.util.Map;
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
        return new ResponseEntity<>(orderService.placeOrder(orderRequest), HttpStatus.CREATED);
    }

    @PostMapping("/{orderId}/confirm-payment")
    public ResponseEntity<OrderResponse> confirmPayment(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(orderService.confirmPayment(orderId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(@PathVariable("customerId") Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<OrderResponse>> getMySecureOrderHistory(
            @RequestHeader(value = "X-Logged-In-User") String username) {
        UserDTO user = userClient.getUserByUserName(username);
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(user.getId()));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable("orderId") Long orderId,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, request.getNewStatus()));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    // THE FIX: New endpoint specifically for Logistics to cancel with a reason
    @PutMapping("/{orderId}/logistics-cancel")
    public ResponseEntity<OrderResponse> logisticsCancelOrder(
            @PathVariable("orderId") Long orderId,
            @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(orderService.logisticsCancelOrder(orderId, payload.get("reason")));
    }

    @PostMapping("/cart/sync")
    public ResponseEntity<Void> syncCart(
            @RequestBody String cartJson, 
            @RequestHeader(value = "X-Logged-In-User") String username) {
        orderService.syncCart(username, cartJson);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable("status") OrderStatus status) {
        List<OrderResponse> filtered = orderService.getAllOrders().stream()
                .filter(o -> o.getOrderStatus() == status)
                .collect(Collectors.toList());
        return ResponseEntity.ok(filtered);
    }
}