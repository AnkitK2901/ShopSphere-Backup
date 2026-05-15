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
  ) { }

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
      // FIXED: Enforced numeric Zip (5-6 digits)
      zipCode: ['', [Validators.required, Validators.pattern('^[0-9]{5,6}$')]],
      // FIXED: Enforced strict 16-digit card number
      cardNumber: [
        '',
        [Validators.required, Validators.pattern('^[0-9]{16}$')],
      ],
      // FIXED: Enforced MM/YY pattern
      // Replace your current expiry line with this:
      expiry: [
        '',
        [
          Validators.required,
          Validators.pattern(/^(0[1-9]|1[0-2])\/?([0-9]{2})$/),
        ],
      ],
      // FIXED: Enforced strict 3-digit CVV
      cvv: ['', [Validators.required, Validators.pattern('^[0-9]{3}$')]],
    });
  }

  placeOrder(): void {
    if (this.checkoutForm.invalid) {
      this.checkoutForm.markAllAsTouched();
      this.toastService.showError(
        'Please complete all required fields (highlighted in red) correctly.',
      );
      return;
    }

    this.isProcessing = true;

    const mappedItems = this.cartItems.map((item) => {
      let optionString = '';
      if (item.selectedOption && item.selectedOption !== 'None') {
        optionString = `${item.selectedOption.type}: ${item.selectedOption.value}`;
      } else {
        optionString = 'None';
      }

      return {
        productId: String(item.productId || item.id),
        quantity: item.quantity || 1,
        selectedOption: optionString,
      };
    });

    const formVals = this.checkoutForm.value;
    const fullAddress = `${formVals.fullName}, ${formVals.shippingAddress}, ${formVals.city}, Zip: ${formVals.zipCode}`;

    this.orderService
      .placeOrder({
        shippingAddress: fullAddress,
        items: mappedItems,
        paymentMode: 'CREDIT_CARD',
        expectedTotal: this.cartTotal,
      })
      .subscribe({
        next: (response: any) => {
          this.orderService.confirmPayment(response.orderId).subscribe({
            next: () => {
              this.cartService.clearCart();
              this.toastService.showSuccess(
                'Payment Successful! Order confirmed.',
              );
              this.router.navigate(['/history']);
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
          this.isProcessing = false;
          const errorMessage = err.error?.message || err.message || '';

          if (
            typeof errorMessage === 'string' &&
            errorMessage.includes('CART_MODIFIED')
          ) {
            const parts = errorMessage.split(':');
            if (parts.length > 1) {
              const deadIds = parts[1]
                .split(',')
                .map((id: string) => parseInt(id.trim(), 10));
              deadIds.forEach((id: number) => {
                if (!isNaN(id)) this.cartService.removeFromCart(id);
              });
            }
            this.toastService.showError(
              'Oops! An item is out of stock. Cart updated.',
            );
            this.router.navigate(['/cart']);
          } else {
            this.toastService.showError('Failed to process payment.');
          }
        },
      });
  }
}
