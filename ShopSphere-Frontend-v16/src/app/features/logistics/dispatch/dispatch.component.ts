import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LogisticsService } from '../../../core/services/logistics.service';

@Component({
  selector: 'app-dispatch',
  templateUrl: './dispatch.component.html',
  styleUrls: ['./dispatch.component.css']
})
export class DispatchComponent implements OnInit {
  shipment: any = null;
  isLoading: boolean = true;
  selectedCarrier: string = 'Delhivery'; // Default mock carrier

  constructor(
    private route: ActivatedRoute,
    private logisticsService: LogisticsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.logisticsService.getShipmentById(id).subscribe({
        next: (data) => {
          this.shipment = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error(err);
          alert('Failed to load shipment details.');
          this.router.navigate(['/logistics/queue']);
        }
      });
    }
  }

  dispatchShipment(): void {
    if (this.shipment) {
      // FIX: Triggers the SHIPPED status, which alerts the downstream Carrier clients in Spring Boot
      this.logisticsService.updateStatus(this.shipment.orderId, 'SHIPPED').subscribe({
        next: () => {
          alert(`Package dispatched via ${this.selectedCarrier}! Tracking generated.`);
          this.router.navigate(['/logistics/queue']);
        },
        error: (err) => {
          console.error(err);
          alert('Failed to dispatch package.');
        }
      });
    }
  }
}