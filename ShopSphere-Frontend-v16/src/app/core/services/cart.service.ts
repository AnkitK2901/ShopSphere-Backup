import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  // Syncs instantly with LocalStorage so carts survive page reloads
  private cartItems: any[] = JSON.parse(localStorage.getItem('shopsphere_cart') || '[]');
  
  // BehaviorSubjects allow the Navbar to instantly update when items are added
  private cartSubject = new BehaviorSubject<any[]>(this.cartItems);
  cartItems$ = this.cartSubject.asObservable();

  constructor() {}

  addToCart(product: any, quantity: number = 1): void {
    // FIX: Check if item already exists in cart. If yes, just update the quantity!
    const existingItemIndex = this.cartItems.findIndex(item => item.productId === product.productId);

    if (existingItemIndex !== -1) {
      this.cartItems[existingItemIndex].quantity += quantity;
    } else {
      this.cartItems.push({ ...product, quantity });
    }

    this.updateCartState();
  }

  removeFromCart(productId: number): void {
    this.cartItems = this.cartItems.filter(item => item.productId !== productId);
    this.updateCartState();
  }

  updateQuantity(productId: number, quantity: number): void {
    const item = this.cartItems.find(item => item.productId === productId);
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
    return this.cartItems.reduce((total, item) => total + (item.basePrice * item.quantity), 0);
  }

  getCartItemCount(): number {
    return this.cartItems.reduce((count, item) => count + item.quantity, 0);
  }

  private updateCartState(): void {
    localStorage.setItem('shopsphere_cart', JSON.stringify(this.cartItems));
    this.cartSubject.next(this.cartItems);
  }
}