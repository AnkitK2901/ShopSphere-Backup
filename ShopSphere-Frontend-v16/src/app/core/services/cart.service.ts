import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  // BehaviorSubject keeps the current state of the cart available anywhere
  private cartItemsSubject = new BehaviorSubject<any[]>(this.loadCart());
  public cartItems$ = this.cartItemsSubject.asObservable();

  constructor() {}

  private loadCart(): any[] {
    const saved = localStorage.getItem('cart');
    return saved ? JSON.parse(saved) : [];
  }

  addToCart(product: any, selectedOption: any, price: number): void {
    const currentCart = this.cartItemsSubject.value;
    const newItem = {
      productId: product.id,
      name: product.name,
      optionName: selectedOption ? selectedOption.optionName : 'Standard',
      optionId: selectedOption ? selectedOption.id : null,
      price: price,
      quantity: 1
    };
    
    currentCart.push(newItem);
    localStorage.setItem('cart', JSON.stringify(currentCart));
    this.cartItemsSubject.next(currentCart);
  }

  removeFromCart(index: number): void {
    const currentCart = this.cartItemsSubject.value;
    currentCart.splice(index, 1);
    localStorage.setItem('cart', JSON.stringify(currentCart));
    this.cartItemsSubject.next(currentCart);
  }

  getCartTotal(): number {
    return this.cartItemsSubject.value.reduce((total, item) => total + (item.price * item.quantity), 0);
  }

  clearCart(): void {
    localStorage.removeItem('cart');
    this.cartItemsSubject.next([]);
  }
}