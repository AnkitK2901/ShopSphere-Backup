package com.shopsphere.order.Controller;

import com.shopsphere.order.DTO.OrderRequest;
import com.shopsphere.order.DTO.OrderResponse;
import com.shopsphere.order.DTO.StatusUpdateRequest;
import com.shopsphere.order.Service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("Received GET request to fetch all orders");
        List<OrderResponse> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        log.info("Received GET request to fetch details for Order ID: {}", orderId);
        OrderResponse order = orderService.getOrderById(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PostMapping("/place")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest orderRequest,
            @RequestHeader("X-Logged-In-User") String username) {
            
        log.info("Order placement initiated by user: {}", username);
        // Note: Ensure your OrderServiceImpl.placeOrder is updated to accept the username!
        OrderResponse order = orderService.placeOrder(orderRequest, username);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long orderId,
            @RequestBody StatusUpdateRequest request,
            @RequestHeader(value = "X-Logged-In-User", required = false) String adminUsername) {
            
        log.info("Status update requested for Order ID: {} to {} by user/system: {}", orderId, request.getNewStatus(), adminUsername);
        OrderResponse order = orderService.updateStatus(orderId, request.getNewStatus());
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("X-Logged-In-User") String username) {
            
        log.info("Order cancellation requested for Order ID: {} by user: {}", orderId, username);
        OrderResponse order = orderService.cancelOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PatchMapping("/{orderId}/return")
    public ResponseEntity<OrderResponse> returnOrder(
            @PathVariable Long orderId,
            @RequestHeader("X-Logged-In-User") String username) {
            
        log.info("Order return requested for Order ID: {} by user: {}", orderId, username);
        OrderResponse order = orderService.returnOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}