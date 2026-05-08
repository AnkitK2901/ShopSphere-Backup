import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css'] // Isolated CSS!
})
export class HistoryComponent implements OnInit {
  orders: any[] = [];
  isLoading: boolean = true;

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.orderService.getMyOrders().subscribe({
      next: (data) => {
        // Sort orders so the newest ones appear at the top
        this.orders = data.sort((a: any, b: any) => b.id - a.id);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load orders', err);
        this.isLoading = false;
      }
    });
  }

  // 🚀 INTERVIEW HIGHLIGHT: Mapping Enums to UI layout dynamically
  getProgressWidth(status: string): string {
    switch(status?.toUpperCase()) {
      case 'PENDING': return '10%';
      case 'CONFIRMED': return '33%';
      case 'PACKED': return '66%';
      case 'SHIPPED': return '85%';
      case 'DELIVERED': return '100%';
      default: return '0%';
    }
  }

  // Dynamic colors for the progress bar
  getProgressColor(status: string): string {
    if (status === 'DELIVERED') return '#27ae60'; // Green for success
    if (status === 'PENDING') return '#f39c12'; // Orange for waiting
    return '#0056b3'; // Blue for in-progress
  }
}