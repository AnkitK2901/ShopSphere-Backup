import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';
import { CatalogService } from '../../../core/services/catalog.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {
  orders: any[] = [];
  isLoading: boolean = true;

  constructor(
    private orderService: OrderService,
    private catalogService: CatalogService // FIX: Injecting catalog to get product names!
  ) {}

  ngOnInit(): void {
    this.orderService.getMyOrders().subscribe({
      next: (data) => {
        // Sort newest first based on the Spring Boot orderId
        const sortedOrders = data.sort((a: any, b: any) => b.orderId - a.orderId);
        
        // FIX: Enrich the raw orders with real product names and images!
        sortedOrders.forEach((order: any) => {
          this.catalogService.getProductById(order.productId).subscribe({
            next: (product) => {
              order.productName = product.name;
              order.productImage = product.previewImage;
            },
            error: () => {
              order.productName = 'Product Details Unavailable';
            }
          });
        });

        this.orders = sortedOrders;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load orders', err);
        this.isLoading = false;
      }
    });
  }

  getProgressWidth(status: string): string {
    switch(status?.toUpperCase()) {
      case 'PENDING_PAYMENT': return '10%';
      case 'CONFIRMED': return '33%';
      case 'PACKED': return '66%';
      case 'SHIPPED': return '85%';
      case 'DELIVERED': return '100%';
      default: return '0%';
    }
  }

  getProgressColor(status: string): string {
    if (status === 'DELIVERED') return '#27ae60'; 
    if (status === 'PENDING_PAYMENT') return '#e74c3c'; // Red for unpaid
    if (status === 'CONFIRMED') return '#f39c12'; // Orange for confirmed
    return '#0056b3'; // Blue for in-progress
  }
}