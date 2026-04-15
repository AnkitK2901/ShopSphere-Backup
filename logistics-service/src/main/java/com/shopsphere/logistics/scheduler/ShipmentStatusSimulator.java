package com.shopsphere.logistics.scheduler;

// import com.shopsphere.logistics.entity.Shipment;
// import com.shopsphere.logistics.entity.ShipmentStatus;
import com.shopsphere.logistics.service.ShipmentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ShipmentStatusSimulator {

    private final ShipmentService shipmentService;

    public ShipmentStatusSimulator(ShipmentService shipmentService)
    {
        this.shipmentService = shipmentService;
    }

    @Scheduled(fixedRate = 60_000)
    public void simulate() {
        shipmentService.simulateShipmentProgress();
    }
}
