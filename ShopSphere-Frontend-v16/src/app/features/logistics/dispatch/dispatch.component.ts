import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LogisticsService } from '../../../core/services/logistics.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-dispatch',
  templateUrl: './dispatch.component.html',
  styleUrls: ['./dispatch.component.css']
})
export class DispatchComponent implements OnInit {
  shipment: any = null;
  isLoading: boolean = true;
  selectedCarrier: string = 'Delhivery'; 

  constructor(
    private route: ActivatedRoute,
    private logisticsService: LogisticsService,
    private router: Router,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.logisticsService.getShipmentById(Number(id)).subscribe({
        next: (data) => {
          this.shipment = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error(err);
          this.toastService.showError('Failed to load shipment details.');
          this.router.navigate(['/logistics/queue']);
        }
      });
    }
  }

  dispatchShipment(): void {
    if (this.shipment) {
      this.logisticsService.updateStatus(this.shipment.orderId, 'IN_TRANSIT', this.selectedCarrier).subscribe({
        next: () => {
          this.toastService.showSuccess(`Package dispatched via ${this.selectedCarrier}! Tracking generated.`);
          this.router.navigate(['/logistics/queue']);
        },
        error: (err) => {
          console.error(err);
          this.toastService.showError('Failed to dispatch package.');
        }
      });
    }
  }
}