import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';
import { OrderService } from '../../../core/services/order.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css'],
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
    private toastService: ToastService,
  ) {}

  ngOnInit(): void {
    this.cartService.cartItems$.subscribe((items) => {
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
      cardNumber: [
        '',
        [Validators.required, Validators.pattern('^[0-9]{16}$')],
      ],
      expiry: ['', Validators.required],
      cvv: ['', [Validators.required, Validators.pattern('^[0-9]{3}$')]],
    });
  }

  placeOrder(): void {
    if (this.checkoutForm.valid) {
      this.isProcessing = true;

      // THE FIX: Safely extract the exact customization choice if it exists
      const mappedItems = this.cartItems.map((item) => {
        let optionString = '';
        if (item.selectedOption) {
          optionString = `${item.selectedOption.type}: ${item.selectedOption.value}`;
        }

        return {
          productId: String(item.productId || item.id),
          quantity: item.quantity || 1,
          selectedOption: optionString, // Pass it to the backend!
        };
      });

      // THE FIX: Combine the form fields into a single readable Address
      const formVals = this.checkoutForm.value;
      const fullAddress = `${formVals.fullName}, ${formVals.shippingAddress}, ${formVals.city}, Zip: ${formVals.zipCode}`;

      this.orderService
        .placeOrder({
          shippingAddress: fullAddress, // Pass it to the backend!
          items: mappedItems,
          paymentMode: 'CREDIT_CARD',
        })
        .subscribe({
          next: (response: any) => {
            this.orderService.confirmPayment(response.orderId).subscribe({
              next: () => {
                this.cartService.clearCart();
                this.toastService.showSuccess(
                  'Payment Successful! Order confirmed and sent to logistics.',
                );
                this.router.navigate(['/orders/history']);
              },
              error: (err) => {
                this.toastService.showError(
                  'Payment confirmed, but logistics sync failed.',
                );
                this.isProcessing = false;
              },
            });
          },
          error: (err) => {
            this.toastService.showError(
              'Failed to process payment. Ensure your backend Microservices are running.',
            );
            this.isProcessing = false;
          },
        });
    } else {
      this.checkoutForm.markAllAsTouched();
    }
  }
}
