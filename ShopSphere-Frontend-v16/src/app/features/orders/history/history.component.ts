import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';
import { CatalogService } from '../../../core/services/catalog.service';
import { forkJoin, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css'],
})
export class HistoryComponent implements OnInit {
  orders: any[] = [];
  isLoading: boolean = true;

  constructor(
    private orderService: OrderService,
    private catalogService: CatalogService,
  ) {}

  ngOnInit(): void {
    this.orderService.getMyOrders().subscribe({
      next: (data) => {
        const sortedOrders = data.sort(
          (a: any, b: any) => b.orderId - a.orderId,
        );

        sortedOrders.forEach((order: any) => {
          if (order.items && order.items.length > 0) {
            const itemRequests = order.items.map((item: any) =>
              this.catalogService.getProductById(item.productId).pipe(
                map((product: any) => {
                  item.productName = product.name;
                  item.productImage = product.previewImage;
                  return item;
                }),
                catchError(() => {
                  item.productName = 'Product Details Unavailable';
                  item.productImage = null;
                  return of(item);
                }),
              ),
            );
            forkJoin(itemRequests).subscribe();
          }
        });

        this.orders = sortedOrders;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load orders', err);
        this.isLoading = false;
      },
    });
  }

  // THE FIX: Cleans the raw backend enum string for the UI
  formatStatus(status: string): string {
    if (!status) return '';
    return status.replace(/_/g, ' ');
  }
  isStatusReached(currentStatus: string, targetStatus: string): boolean {
    // FIX: Added the new status to the visual array
    const flow = [
      'PENDING_PAYMENT',
      'CONFIRMED',
      'PACKED',
      'SHIPPED',
      'OUT_FOR_DELIVERY',
      'DELIVERED',
    ];
    const currentIndex = flow.indexOf(currentStatus?.toUpperCase());
    const targetIndex = flow.indexOf(targetStatus?.toUpperCase());
    return currentIndex >= targetIndex && currentIndex !== -1;
  }

  getProgressWidth(status: string): string {
    // FIX: Math updated for 5 dots
    switch (status?.toUpperCase()) {
      case 'PENDING_PAYMENT':
        return '0%';
      case 'CONFIRMED':
        return '0%';
      case 'PACKED':
        return '25%';
      case 'SHIPPED':
        return '50%';
      case 'OUT_FOR_DELIVERY':
        return '75%';
      case 'DELIVERED':
        return '100%';
      case 'CANCELLED':
        return '0%';
      case 'RETURNED':
        return '0%';
      default:
        return '0%';
    }
  }

  getProgressColor(status: string): string {
    const safeStatus = status?.toUpperCase();
    if (safeStatus === 'DELIVERED') return '#27ae60';
    if (safeStatus === 'PENDING_PAYMENT' || safeStatus === 'CANCELLED')
      return '#e74c3c';
    if (safeStatus === 'CONFIRMED') return '#f39c12';
    return '#3182ce';
  }
  // THE FIX: Trigger the Saga Rollback from the UI
  cancelOrder(orderId: number): void {
    if (confirm('Are you sure you want to cancel this order? This action cannot be undone.')) {
      this.orderService.cancelOrder(orderId).subscribe({
        next: () => {
          // Refresh the page to show the CANCELLED status
          this.ngOnInit();
        },
        error: (err) => console.error('Failed to cancel order', err)
      });
    }
  }

  returnOrder(orderId: number): void {
    if (confirm('Initiate a return for this order?')) {
      this.orderService.returnOrder(orderId).subscribe({
        next: () => {
          // Refresh the page to show the RETURNED status
          this.ngOnInit();
        },
        error: (err) => console.error('Failed to return order', err)
      });
    }
  }
}
