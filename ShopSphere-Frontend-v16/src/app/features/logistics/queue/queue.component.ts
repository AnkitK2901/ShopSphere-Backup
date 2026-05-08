import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-queue',
  templateUrl: './queue.component.html',
  styleUrls: ['./queue.component.css'] // Isolated CSS!
})
export class QueueComponent implements OnInit {
  activeOrders: any[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchQueue();
  }

  fetchQueue(): void {
    // Assuming you have an endpoint for ALL orders (admin/logistics view)
    // If you only have getMyOrders(), you might need to adjust this depending on your backend
    this.orderService.getMyOrders().subscribe({
      next: (orders) => {
        // Filter out completed orders to create the "Queue"
        this.activeOrders = orders.filter((o: any) => 
          o.status === 'PENDING' || o.status === 'CONFIRMED' || o.status === 'PACKED'
        );
        // Sort oldest first (First In, First Out)
        this.activeOrders.sort((a: any, b: any) => a.id - b.id);
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Failed to load the fulfillment queue. Check backend connection.';
        this.isLoading = false;
      }
    });
  }

  // Quick action to update status
  updateStatus(orderId: number, newStatus: string): void {
    this.orderService.updateOrderStatus(orderId, newStatus).subscribe({
      next: () => {
        alert(`Order #${orderId} updated to ${newStatus}`);
        this.fetchQueue(); // Refresh the list
      },
      error: (err) => {
        alert('Failed to update status.');
        console.error(err);
      }
    });
  }

  // Navigate to specific worker screens
  goToPacking(orderId: number): void {
    this.router.navigate(['/logistics/pack', orderId]);
  }

  goToDispatch(orderId: number): void {
    this.router.navigate(['/logistics/dispatch', orderId]);
  }
}