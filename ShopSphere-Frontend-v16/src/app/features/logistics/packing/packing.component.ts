import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LogisticsService } from '../../../core/services/logistics.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-packing',
  templateUrl: './packing.component.html',
  styleUrls: ['./packing.component.css']
})
export class PackingComponent implements OnInit {
  shipment: any = null;
  orderItems: any[] = [];
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private logisticsService: LogisticsService,
    private router: Router,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.logisticsService.getEnrichedShipmentByOrderId(Number(id)).subscribe({
        next: (data: any) => {
          this.shipment = data.shipment;
          this.orderItems = data.items.map((item: any) => ({
            ...item,
            packed: false 
          }));
          this.isLoading = false;
        },
        // THE FIX: Added ': any' to satisfy TypeScript's strict mode
        error: (err: any) => {
          this.toastService.showError('Failed to load enriched shipment data.');
          this.router.navigate(['/logistics/queue']);
        }
      });
    }
  }

  toggleItemPacked(item: any): void {
    item.packed = !item.packed;
  }

  get allItemsPacked(): boolean {
    return this.orderItems.length > 0 && this.orderItems.every(i => i.packed);
  }

  formatStatus(status: string): string {
    return status ? status.replace(/_/g, ' ') : '';
  }

  markAsPacked(): void {
    if (this.shipment && this.allItemsPacked) {
      this.logisticsService.updateStatus(this.shipment.orderId, 'PACKED').subscribe({
        next: () => {
          this.toastService.showSuccess('Shipment Packed! Moving to Dispatch queue.');
          this.router.navigate(['/logistics/queue']);
        },
        error: (err: any) => this.toastService.showError('Status update failed.')
      });
    }
  }
}