package com.shopsphere.order.Service;

import com.shopsphere.order.DTO.*;
import com.shopsphere.order.Entity.OrderEntity;
import com.shopsphere.order.Entity.OrderItemEntity;
import com.shopsphere.order.Enums.OrderStatus;
import com.shopsphere.order.Repository.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private UserClient userClient;
    @Mock private ProductClient productClient;
    @Mock private LogisticsClient logisticsClient;
    @Mock private InventoryClient inventoryClient;
    @Mock private AnalyticsClient analyticsClient;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest validRequest;
    private ProductDTO mockProduct;
    private UserDTO mockUser;
    private OrderEntity mockOrder;

    @BeforeEach
    void setUp() {
        mockUser = new UserDTO();
        mockUser.setId(1L);
        mockUser.setUserName("ankit_admin");

        mockProduct = new ProductDTO();
        // FIX 5: Use a Long ID
        mockProduct.setProductId(999L);
        mockProduct.setTotalPrice(500.0);

        validRequest = new OrderRequest();
        validRequest.setUserName("ankit_admin");
        validRequest.setPaymentMode("CREDIT_CARD");
        
        OrderItemRequest itemReq = new OrderItemRequest();
        // FIX 6: Use a Long ID
        itemReq.setProductId(999L);
        itemReq.setQuantity(2);
        validRequest.setItems(List.of(itemReq));

        mockOrder = new OrderEntity();
        mockOrder.setOrderId(100L);
        mockOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        mockOrder.setTotalAmount(1000.0);
        
        OrderItemEntity itemEntity = new OrderItemEntity();
        // FIX 7: Use a Long ID
        itemEntity.setProductId(999L);
        itemEntity.setQuantity(2);
        mockOrder.setItems(List.of(itemEntity));
    }

    @Test
    void placeOrder_Success() {
        when(userClient.getUserByUserName("ankit_admin")).thenReturn(mockUser);
        // FIX 8: Update mock to expect Long
        when(productClient.findProductById(999L)).thenReturn(mockProduct);
        
        OrderEntity savedOrder = new OrderEntity();
        savedOrder.setOrderId(100L);
        savedOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        savedOrder.setItems(new ArrayList<>());
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.placeOrder(validRequest);

        assertNotNull(response);
        assertEquals(100L, response.getOrderId());
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        
        verify(inventoryClient, never()).deductStock(any(StockRequest.class));
    }

    @Test
    void confirmPayment_Success_TriggersSagaAndAnalytics() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(mockOrder);

        OrderResponse response = orderService.confirmPayment(100L);

        assertNotNull(response); 
        assertEquals(OrderStatus.CONFIRMED, mockOrder.getStatus());
        verify(inventoryClient, times(1)).deductStock(any(StockRequest.class));
        verify(analyticsClient, times(1)).logRevenueEvent(1000.0);
    }

    @Test
    void confirmPayment_DatabaseFails_TriggersSagaRollback() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(mockOrder));
        
        when(orderRepository.save(any(OrderEntity.class))).thenThrow(new RuntimeException("DB Connection Lost"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.confirmPayment(100L);
        });

        assertTrue(exception.getMessage().contains("Payment confirmation failed"));
        
        verify(inventoryClient, times(1)).deductStock(any(StockRequest.class));
        verify(inventoryClient, times(1)).refundStock(any(StockRequest.class));
    }
}