import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';
import { OrderService } from '../../../core/services/order.service';
import { forkJoin, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { ToastService } from '../../../core/services/toast.service';
@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {
  checkoutForm!: FormGroup;
  cartItems: any[] = [];
  cartTotal: number = 0;
  isProcessing: boolean = false;

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.cartService.cartItems$.subscribe(items => {
      this.cartItems = items;
      this.cartTotal = this.cartService.getCartTotal();
    });

    if (this.cartItems.length === 0) {
      this.router.navigate(['/cart']);
    }

    this.checkoutForm = this.fb.group({
      fullName: ['', Validators.required],
      shippingAddress: ['', Validators.required],
      city: ['', Validators.required],
      zipCode: ['', [Validators.required, Validators.pattern('^[0-9]{5,6}$')]],
      cardNumber: ['', [Validators.required, Validators.pattern('^[0-9]{16}$')]],
      expiry: ['', Validators.required],
      cvv: ['', [Validators.required, Validators.pattern('^[0-9]{3}$')]]
    });
  }

  placeOrder(): void {
    if (this.checkoutForm.valid) {
      this.isProcessing = true;
      
      const orderObservables = this.cartItems.map(item => {
        // FIX: The Data-Mismatch Failsafe. Ensures the ID is never null.
        const safeProductId = item.productId || item.id; 

        return this.orderService.placeOrder({
          productId: String(safeProductId),
          quantity: item.quantity || 1,
          paymentMode: "CREDIT_CARD" 
        }).pipe(
          switchMap((response: any) => {
            return this.orderService.confirmPayment(response.orderId);
          })
        );
      });

      forkJoin(orderObservables).subscribe({
        next: () => {
          this.cartService.clearCart();
          this.toastService.showSuccess('Payment Successful! Orders confirmed and sent to logistics. Redirecting...');
          this.router.navigate(['/orders/history']);
        },
        error: (err) => {
          console.error('Order Placement Failed:', err);
          this.toastService.showError('Failed to process payment. Ensure your backend Microservices are running.');
          this.isProcessing = false;
        }
      });
    } else {
      this.checkoutForm.markAllAsTouched();
    }
  }
}