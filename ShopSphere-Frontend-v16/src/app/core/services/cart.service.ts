import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private cartItems: any[] = JSON.parse(
    localStorage.getItem('shopsphere_cart') || '[]',
  );

  private cartSubject = new BehaviorSubject<any[]>(this.cartItems);
  cartItems$ = this.cartSubject.asObservable();

  constructor(private http: HttpClient) {}

  addToCart(product: any, quantity: number = 1): void {
    const existingItemIndex = this.cartItems.findIndex((item) => {
      // FIX: Safely check IDs so undefined doesn't match undefined
      const incomingId = product.productId || product.id;
      const existingId = item.productId || item.id;
      const isSameProduct = incomingId === existingId;

      const isSameOption =
        (!item.selectedOption && !product.selectedOption) ||
        item.selectedOption?.id === product.selectedOption?.id;

      return isSameProduct && isSameOption;
    });

    if (existingItemIndex !== -1) {
      this.cartItems[existingItemIndex].quantity += quantity;
    } else {
      const newItem = { ...product, quantity };
      if (!newItem.productId && newItem.id) {
        newItem.productId = newItem.id;
      }
      this.cartItems.push(newItem);
    }

    this.updateCartState();
  }

  removeFromCart(productId: number, optionId?: number): void {
    this.cartItems = this.cartItems.filter((item) => {
      if (optionId !== undefined) {
        return !(
          item.productId === productId && item.selectedOption?.id === optionId
        );
      }
      return item.productId !== productId;
    });
    this.updateCartState();
  }

  updateQuantity(productId: number, quantity: number, optionId?: number): void {
    const item = this.cartItems.find((item) => {
      if (optionId !== undefined) {
        return (
          item.productId === productId && item.selectedOption?.id === optionId
        );
      }
      return item.productId === productId;
    });

    if (item && quantity > 0) {
      item.quantity = quantity;
      this.updateCartState();
    }
  }

  clearCart(): void {
    this.cartItems = [];
    this.updateCartState();
  }

  getCartTotal(): number {
    const total = this.cartItems.reduce(
      (acc, item) => acc + item.basePrice * item.quantity,
      0,
    );
    return Math.round((total + Number.EPSILON) * 100) / 100;
  }

  getCartItemCount(): number {
    return this.cartItems.reduce((count, item) => count + item.quantity, 0);
  }

  private updateCartState(): void {
    localStorage.setItem('shopsphere_cart', JSON.stringify(this.cartItems));
    this.cartSubject.next(this.cartItems);
    
    this.syncWithBackend();
  }

  private syncWithBackend(): void {
    // The AuthInterceptor automatically applies the JWT here!
    this.http.post('http://localhost:9090/api/orders/cart/sync', JSON.stringify(this.cartItems))
      .subscribe({
        error: (err) => console.error('Silent cart sync failed', err)
      });
  }
}