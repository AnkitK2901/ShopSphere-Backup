import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LogisticsService } from '../../../core/services/logistics.service';
import { ToastService } from '../../../core/services/toast.service';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-packing',
  templateUrl: './packing.component.html',
  styleUrls: ['./packing.component.css']
})
export class PackingComponent implements OnInit {
  shipment: any = null;
  // THE FIX: Define the property that the HTML is looking for
  orderItems: any[] = []; 
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private logisticsService: LogisticsService,
    private router: Router,
    private toastService: ToastService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      // Step 1: Fetch enriched data from Logistics
      this.logisticsService.getEnrichedShipmentByOrderId(Number(id)).subscribe({
        next: (data: any) => {
          this.shipment = data.shipment;
          // THE FIX: Assign the items from the enriched response to the class property
          this.orderItems = data.items.map((item: any) => ({
            ...item,
            packed: false 
          }));
          this.isLoading = false;
        },
        error: (err: any) => {
          this.toastService.showError('Failed to load enriched shipment data.');
          this.router.navigate(['/logistics/queue']);
        }
      });
    }
  }

  formatStatus(status: string): string {
    if (!status) return '';
    return status.replace(/_/g, ' ');
  }

  toggleItemPacked(item: any): void {
    item.packed = !item.packed;
  }

  get allItemsPacked(): boolean {
    // THE FIX: Check the defined class property
    return this.orderItems.length > 0 && this.orderItems.every(i => i.packed);
  }

  markAsPacked(): void {
    if (this.shipment && this.allItemsPacked) {
      this.logisticsService.updateStatus(this.shipment.orderId, 'PACKED').subscribe({
        next: () => {
          this.toastService.showSuccess('Shipment marked as PACKED. Ready for dispatch.');
          this.router.navigate(['/logistics/queue']);
        },
        error: (err: any) => {
          this.toastService.showError('Failed to update status.');
        }
      });
    }
  }
}