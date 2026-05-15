import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LogisticsService } from '../../../core/services/logistics.service';
import { ToastService } from '../../../core/services/toast.service';
import { OrderService } from '../../../core/services/order.service';
import { CatalogService } from '../../../core/services/catalog.service';
import { forkJoin, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

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
    private toastService: ToastService,
    private orderService: OrderService,
    private catalogService: CatalogService // Added Catalog Service
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      // Step 1: Fetch the Shipment
      this.logisticsService.getShipmentById(Number(id)).subscribe({
        next: (shipmentData: any) => {
          this.shipment = shipmentData;
          
          // Step 2: Fetch the Order securely using the Frontend JWT Token
          this.orderService.getOrderById(Number(this.shipment.orderId)).subscribe({
             next: (orderData: any) => { 
                this.shipment.customerId = orderData.customerId;
                const rawItems = orderData.items || [];
                
                if (rawItems.length === 0) {
                  this.orderItems = [];
                  this.isLoading = false;
                  return;
                }

                // Step 3: Fetch the Images & Names securely from the Catalog
                const itemRequests = rawItems.map((item: any) => {
                   item.packed = false; // Add UI toggle state
                   return this.catalogService.getProductById(item.productId).pipe(
                      map((product: any) => {
                         item.name = product.name;
                         item.previewImage = product.previewImage;
                         return item;
                      }),
                      catchError(() => {
                         item.name = 'Product ID: ' + item.productId;
                         item.previewImage = null;
                         return of(item);
                      })
                   );
                });

                // Wait for all images to load before displaying
                forkJoin(itemRequests).subscribe((enrichedItems: any) => {
                   this.orderItems = enrichedItems;
                   this.isLoading = false;
                });
             },
             error: (err) => {
                 this.toastService.showError('Failed to load secure order items.');
                 this.isLoading = false;
             }
          });
        },
        error: (err) => {
          this.toastService.showError('Failed to load shipment details.');
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