package com.shopsphere.logistics.service;

import com.shopsphere.logistics.carrier.MockDelhiveryClient;
import com.shopsphere.logistics.carrier.MockShiprocketClient;
import com.shopsphere.logistics.entity.Shipment;
import com.shopsphere.logistics.entity.ShipmentStatus;
import com.shopsphere.logistics.exception.InvalidShipmentStatusException;
import com.shopsphere.logistics.exception.ShipmentAlreadyExistsException;
import com.shopsphere.logistics.exception.ShipmentNotFoundException;
import com.shopsphere.logistics.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository repository;

    @Mock
    private MockDelhiveryClient delhiveryClient;

    @Mock
    private MockShiprocketClient shiprocketClient;

    @InjectMocks
    private ShipmentService shipmentService;

    private Shipment shipment;

    @BeforeEach
    void setUp() {
        shipment = new Shipment();
        shipment.setShipmentId("ship-123");
        shipment.setOrderId("order-123");
        shipment.setTrackingNumber("track-001");
        shipment.setStatus(ShipmentStatus.CREATED);
        shipment.setUpdatedAt(LocalDateTime.now().minusMinutes(10));
    }

    @Test
    void shouldReturnAllShipments() {
        when(repository.findAll()).thenReturn(List.of(shipment));

        List<Shipment> result = shipmentService.getAllShipments();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void shouldCreateShipmentSuccessfully() {
        String orderId = "order-123";

        when(repository.findByOrderId(orderId)).thenReturn(Optional.empty());

        if (orderId.hashCode() % 2 == 0) {
            when(delhiveryClient.createShipment(orderId)).thenReturn(shipment);
        } else {
            when(shiprocketClient.createShipment(orderId)).thenReturn(shipment);
        }

        when(repository.save(any(Shipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Shipment result = shipmentService.createShipment(orderId);

        assertNotNull(result.getShipmentId());
        assertEquals(orderId, result.getOrderId());
        assertEquals(ShipmentStatus.CREATED, result.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenShipmentAlreadyExists() {
        when(repository.findByOrderId("order-123"))
                .thenReturn(Optional.of(shipment));

        assertThrows(
                ShipmentAlreadyExistsException.class,
                () -> shipmentService.createShipment("order-123")
        );
    }

    @Test
    void shouldGetShipmentByOrderId() {
        when(repository.findByOrderId("order-123"))
                .thenReturn(Optional.of(shipment));

        Shipment result = shipmentService.getShipmentByOrderId("order-123");

        assertEquals("order-123", result.getOrderId());
    }

    @Test
    void shouldThrowExceptionWhenShipmentNotFound() {
        when(repository.findByOrderId("order-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                ShipmentNotFoundException.class,
                () -> shipmentService.getShipmentByOrderId("order-999")
        );
    }

    @Test
    void shouldUpdateShipmentStatusSuccessfully() {
        shipment.setStatus(ShipmentStatus.CREATED);

        when(repository.findByOrderId("order-123"))
                .thenReturn(Optional.of(shipment));
        when(repository.save(any(Shipment.class))).thenReturn(shipment);

        Shipment result =
                shipmentService.updateShipmentStatusByOrderId("order-123", "picked_up");

        assertEquals(ShipmentStatus.PICKED_UP, result.getStatus());
    }

    @Test
    void shouldThrowExceptionForInvalidStatusValue() {
        assertThrows(
                InvalidShipmentStatusException.class,
                () -> shipmentService.updateShipmentStatusByOrderId("order-123", "INVALID")
        );
    }

    @Test
    void shouldThrowExceptionForInvalidStatusTransition() {
        shipment.setStatus(ShipmentStatus.CREATED);

        when(repository.findByOrderId("order-123"))
                .thenReturn(Optional.of(shipment));

        assertThrows(
                InvalidShipmentStatusException.class,
                () -> shipmentService.updateShipmentStatusByOrderId("order-123", "DELIVERED")
        );
    }

    @Test
    void shouldProgressShipmentStatus() {
        shipment.setStatus(ShipmentStatus.CREATED);
        shipment.setUpdatedAt(LocalDateTime.now().minusMinutes(2));

        when(repository.findByStatusNot(ShipmentStatus.DELIVERED))
                .thenReturn(List.of(shipment));
        when(repository.save(any(Shipment.class))).thenReturn(shipment);

        shipmentService.simulateShipmentProgress();

        verify(repository).save(shipment);
        assertEquals(ShipmentStatus.PICKED_UP, shipment.getStatus());
    }
}
