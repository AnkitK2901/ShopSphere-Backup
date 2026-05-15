import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LogisticsService } from '../../../core/services/logistics.service';
import { OrderService } from '../../../core/services/order.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-queue',
  templateUrl: './queue.component.html',
  styleUrls: ['./queue.component.css'],
})
export class QueueComponent implements OnInit {
  activeShipments: any[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  showCancelModal: boolean = false;
  orderToCancel: string | null = null;

  constructor(
    private logisticsService: LogisticsService,
    private orderService: OrderService, 
    private router: Router,
    private toastService: ToastService,
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
          // THE FIX: Added .trim() to ensure no spacing bugs let cancelled orders slip through
          this.activeShipments = shipments.filter((s: any) => {
             const safeStatus = s.status ? s.status.toUpperCase().trim() : '';
             return safeStatus !== 'DELIVERED' && safeStatus !== 'CANCELLED';
          });
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Failed to load the fulfillment queue. Check backend connection.';
        this.isLoading = false;
      },
    });
  }

  formatStatus(status: string): string {
    if (!status) return '';
    return status.replace(/_/g, ' ');
  }

  goToPacking(orderId: string): void {
    this.router.navigate(['/logistics/packing', orderId]);
  }

  goToDispatch(orderId: string): void {
    this.router.navigate(['/logistics/dispatch', orderId]);
  }

  openCancelModal(orderId: string): void {
    this.orderToCancel = orderId;
    this.showCancelModal = true;
  }

  closeCancelModal(): void {
    this.showCancelModal = false;
    this.orderToCancel = null;
  }

  confirmLogisticsCancel(): void {
    if (this.orderToCancel) {
      const reason = "Cancelled by Logistics department due to unavoidable circumstances.";
      this.orderService.logisticsCancelOrder(Number(this.orderToCancel), reason).subscribe({
        next: () => {
          this.toastService.showSuccess(`Order #${this.orderToCancel} cancelled successfully.`);
          this.closeCancelModal();
          this.fetchQueue(); 
        },
        error: (err) => {
          this.toastService.showError('Failed to cancel order.');
          console.error(err);
          this.closeCancelModal();
        }
      });
    }
  }
}