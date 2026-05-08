import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css'] // Isolated CSS!
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
    private router: Router
  ) {}

  ngOnInit(): void {
    // 1. Grab cart data
    this.cartService.cartItems$.subscribe(items => {
      this.cartItems = items;
      this.cartTotal = this.cartService.getCartTotal();
    });

    // 2. Security Check: Don't let them checkout an empty cart!
    if (this.cartItems.length === 0) {
      this.router.navigate(['/cart']);
    }

    // 3. Initialize the Form
    this.checkoutForm = this.fb.group({
      fullName: ['', Validators.required],
      shippingAddress: ['', Validators.required],
      city: ['', Validators.required],
      zipCode: ['', [Validators.required, Validators.pattern('^[0-9]{5,6}$')]],
      
      // Mock Payment Details
      cardNumber: ['', [Validators.required, Validators.pattern('^[0-9]{16}$')]],
      expiry: ['', Validators.required],
      cvv: ['', [Validators.required, Validators.pattern('^[0-9]{3}$')]]
    });
  }

  placeOrder(): void {
    if (this.checkoutForm.valid) {
      this.isProcessing = true;
      
      // Construct the payload for Spring Boot
      const orderPayload = {
        items: this.cartItems,
        shippingDetails: this.checkoutForm.value,
        totalAmount: this.cartTotal
      };

      this.orderService.placeOrder(orderPayload).subscribe({
        next: (response) => {
          // Success! Clear the cart and send to history
          this.cartService.clearCart();
          alert('Order placed successfully! Redirecting to your dashboard...');
          this.router.navigate(['/orders/history']);
        },
        error: (err) => {
          console.error(err);
          alert('Failed to process order. Please try again.');
          this.isProcessing = false;
        }
      });
    } else {
      // Force validation messages to show if they click submit early
      this.checkoutForm.markAllAsTouched();
    }
  }
}