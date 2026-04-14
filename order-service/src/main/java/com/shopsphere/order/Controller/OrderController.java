package com.shopsphere.order.Controller;

import com.shopsphere.order.DTO.OrderRequest;
import com.shopsphere.order.DTO.OrderResponse;
import com.shopsphere.order.DTO.PaymentRequest;
import com.shopsphere.order.DTO.StatusUpdateRequest;
import com.shopsphere.order.Service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Order Management", description = "APIs for managing the e-commerce order lifecycle and payments")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get all orders")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(){
        List<OrderResponse> order = orderService.getAllOrders();
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @Operation(summary = "Get a specific order by ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId){
        OrderResponse order = orderService.getOrderById(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @Operation(summary = "Place a new order (Validates Inventory automatically)")
    @PostMapping("/place")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderEntity){
        OrderResponse orders = orderService.placeOrder(orderEntity);
        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    // --- LLD REQUIRED: Payment Endpoint ---
    @Operation(summary = "Process Payment via Razorpay/Stripe (Mock)")
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderResponse> processPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentRequest request){
        log.info("REST request to process payment for ID: {}", orderId);
        OrderResponse order = orderService.processPayment(orderId, request);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
    // --------------------------------------

    @Operation(summary = "Update the status of an order (e.g., PACKED, SHIPPED)")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long orderId,
            @RequestBody StatusUpdateRequest request
            ){
        OrderResponse order = orderService.updateStatus(orderId, request.getNewStatus());
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @Operation(summary = "Cancel an order and auto-refund")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId){
        OrderResponse order = orderService.cancelOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @Operation(summary = "Return a delivered order and auto-refund")
    @PatchMapping("/{orderId}/return")
    public ResponseEntity<OrderResponse> returnOrder(@PathVariable Long orderId){
        OrderResponse order = orderService.returnOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}