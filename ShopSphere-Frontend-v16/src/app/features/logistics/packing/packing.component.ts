import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-packing',
  templateUrl: './packing.component.html',
  styleUrls: ['./packing.component.css']
})
export class PackingComponent implements OnInit {
  orderId: number = 0;
  order: any = null;
  packedItemsCount: number = 0;

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.orderId = Number(this.route.snapshot.paramMap.get('id'));
    // Fetch the specific order (Mocking the fetch by getting all and filtering for now)
    this.orderService.getMyOrders().subscribe(orders => {
      this.order = orders.find((o: any) => o.id === this.orderId);
    });
  }

  toggleItemPacked(event: any): void {
    if (event.target.checked) {
      this.packedItemsCount++;
    } else {
      this.packedItemsCount--;
    }
  }

  completePacking(): void {
    this.orderService.updateOrderStatus(this.orderId, 'PACKED').subscribe(() => {
      alert('Order marked as PACKED successfully.');
      this.router.navigate(['/logistics/queue']);
    });
  }
}