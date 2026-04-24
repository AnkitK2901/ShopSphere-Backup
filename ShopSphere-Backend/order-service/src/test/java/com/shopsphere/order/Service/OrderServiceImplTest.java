package com.shopsphere.order.Service;

import com.shopsphere.order.DTO.*;
import com.shopsphere.order.Entity.OrderEntity;
import com.shopsphere.order.Enums.OrderStatus;
import com.shopsphere.order.Repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private UserClient userClient;
    @Mock
    private ProductClient productClient;
    @Mock
    private LogisticsClient logisticsClient;
    @Mock
    private InventoryClient inventoryClient;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest validRequest;
    private ProductDTO mockProduct;
    private UserDTO mockUser;

    @BeforeEach
    void setUp() {
        validRequest = new OrderRequest();
        validRequest.setUserName("ankit_admin");
        validRequest.setProductId("PROD-999");
        validRequest.setQuantity(2);
        validRequest.setPaymentMode("CREDIT_CARD");

        mockProduct = new ProductDTO();
        mockProduct.setProductId("PROD-999");
        mockProduct.setTotalPrice(500.0);

        mockUser = new UserDTO();
        mockUser.setId(1L);
        mockUser.setUserName("ankit_admin");
    }

    @Test
    void placeOrder_Success() {
        // 1. Arrange (Mock the external services)
        when(productClient.findProductById("PROD-999")).thenReturn(mockProduct);
        when(userClient.getUserByUserName("ankit_admin")).thenReturn(mockUser);
        when(inventoryClient.checkStock(any(StockRequest.class))).thenReturn(ResponseEntity.ok(true));
        
        OrderEntity savedOrder = new OrderEntity();
        savedOrder.setOrderId(100L);
        savedOrder.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        // 2. Act
        OrderResponse response = orderService.placeOrder(validRequest);

        // 3. Assert
        assertNotNull(response);
        assertEquals(100L, response.getOrderId());
        verify(inventoryClient, times(1)).deductStock(any(StockRequest.class));
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void placeOrder_DatabaseFails_TriggersSagaRollback() {
        // 1. Arrange
        when(productClient.findProductById("PROD-999")).thenReturn(mockProduct);
        when(userClient.getUserByUserName("ankit_admin")).thenReturn(mockUser);
        when(inventoryClient.checkStock(any(StockRequest.class))).thenReturn(ResponseEntity.ok(true));
        
        // Force the local database to crash during save
        when(orderRepository.save(any(OrderEntity.class))).thenThrow(new RuntimeException("DB Connection Lost"));

        // 2. Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.placeOrder(validRequest);
        });

        assertTrue(exception.getMessage().contains("Order could not be saved"));
        
        // 3. PROOF OF SAGA: Verify that the system automatically called refundStock to release the trapped inventory!
        verify(inventoryClient, times(1)).deductStock(any(StockRequest.class));
        verify(inventoryClient, times(1)).refundStock(any(StockRequest.class));
    }

    @Test
    void placeOrder_InsufficientStock_ThrowsException() {
        // 1. Arrange
        when(productClient.findProductById("PROD-999")).thenReturn(mockProduct);
        when(userClient.getUserByUserName("ankit_admin")).thenReturn(mockUser);
        when(inventoryClient.checkStock(any(StockRequest.class))).thenReturn(ResponseEntity.ok(false));

        // 2. Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            orderService.placeOrder(validRequest);
        });

        // 3. Assert we never tried to deduct stock or save a ghost order
        verify(inventoryClient, never()).deductStock(any(StockRequest.class));
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }
}