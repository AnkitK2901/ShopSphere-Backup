import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-monitor',
  templateUrl: './monitor.component.html',
  styleUrls: ['./monitor.component.css']
})
export class MonitorComponent implements OnInit {
  stats = {
    pending: 0,
    packed: 0,
    shipped: 0,
    totalRevenue: 0
  };

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.orderService.getMyOrders().subscribe(orders => {
      orders.forEach((o: any) => {
        if (o.status === 'PENDING' || o.status === 'CONFIRMED') this.stats.pending++;
        if (o.status === 'PACKED') this.stats.packed++;
        if (o.status === 'SHIPPED' || o.status === 'DELIVERED') {
          this.stats.shipped++;
          this.stats.totalRevenue += o.totalAmount;
        }
      });
    });
  }
}