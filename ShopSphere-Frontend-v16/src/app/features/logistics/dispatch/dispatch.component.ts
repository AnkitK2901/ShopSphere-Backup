import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-dispatch',
  templateUrl: './dispatch.component.html',
  styleUrls: ['./dispatch.component.css']
})
export class DispatchComponent implements OnInit {
  orderId: number = 0;
  dispatchForm!: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.orderId = Number(this.route.snapshot.paramMap.get('id'));
    this.dispatchForm = this.fb.group({
      courier: ['FedEx', Validators.required],
      trackingNumber: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  dispatchOrder(): void {
    if (this.dispatchForm.valid) {
      // In a real app, you would send the tracking number to the backend here too
      this.orderService.updateOrderStatus(this.orderId, 'SHIPPED').subscribe(() => {
        alert('Shipping Label Generated! Order Dispatched.');
        this.router.navigate(['/logistics/queue']);
      });
    }
  }
}