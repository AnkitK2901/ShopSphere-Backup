package com.shopsphere.order.Controller;

import com.shopsphere.order.DTO.OrderRequest;
import com.shopsphere.order.DTO.OrderResponse;
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
@Tag(name = "Order Management", description = "APIs for managing the e-commerce order lifecycle")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get all orders")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(){
        log.info("REST request to get all orders");
        List<OrderResponse> order = orderService.getAllOrders();
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @Operation(summary = "Get a specific order by ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId){
        log.info("REST request to get order : {}", orderId);
        OrderResponse order = orderService.getOrderById(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @Operation(summary = "Place a new order (Validates Inventory automatically)")
    @PostMapping("/place")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderEntity){
        log.info("REST request to place a new order");
        OrderResponse orders = orderService.placeOrder(orderEntity);
        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    @Operation(summary = "Update the status of an order (e.g., PACKED, SHIPPED)")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long orderId,
            @RequestBody StatusUpdateRequest request
            ){
        log.info("REST request to update order status for ID: {}", orderId);
        OrderResponse order = orderService.updateStatus(orderId, request.getNewStatus());
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @Operation(summary = "Cancel an order")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId){
        log.info("REST request to cancel order ID: {}", orderId);
        OrderResponse order = orderService.cancelOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @Operation(summary = "Return a delivered order")
    @PatchMapping("/{orderId}/return")
    public ResponseEntity<OrderResponse> returnOrder(@PathVariable Long orderId){
        log.info("REST request to return order ID: {}", orderId);
        OrderResponse order = orderService.returnOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}