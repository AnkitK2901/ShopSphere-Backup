import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LogisticsService } from '../../../core/services/logistics.service'; 
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-queue',
  templateUrl: './queue.component.html',
  styleUrls: ['./queue.component.css']
})
export class QueueComponent implements OnInit {
  activeShipments: any[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private logisticsService: LogisticsService,
    private router: Router,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.fetchQueue();
  }

  fetchQueue(): void {
    this.logisticsService.getAllShipments().subscribe({
      next: (shipments) => {
        if (!shipments) {
          this.activeShipments = [];
        } else {
          // FIX: Changed from shipmentStatus to status to match Backend JSON
          this.activeShipments = shipments.filter((s: any) => 
            s.status?.toUpperCase() !== 'DELIVERED'
          );
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Failed to load the fulfillment queue. Check backend connection.';
        this.isLoading = false;
      }
    });
  }

  updateStatus(orderId: string, newStatus: string): void {
    this.logisticsService.updateStatus(Number(orderId), newStatus).subscribe({
      next: () => {
        this.toastService.showSuccess(`Shipment for Order #${orderId} updated to ${newStatus}`);
        this.fetchQueue(); 
      },
      error: (err) => {
        this.toastService.showError('Failed to update status.');
        console.error(err);
      }
    });
  }

  goToPacking(orderId: string): void {
    this.router.navigate(['/logistics/pack', orderId]);
  }

  goToDispatch(orderId: string): void {
    this.router.navigate(['/logistics/dispatch', orderId]);
  }
}