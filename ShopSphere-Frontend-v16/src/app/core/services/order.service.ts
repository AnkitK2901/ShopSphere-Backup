import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:9090/api/orders';

  constructor(private http: HttpClient, private authService: AuthService) {}

  placeOrder(orderData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/place`, orderData);
  }

  confirmPayment(orderId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${orderId}/confirm-payment`, {});
  }

  getMyOrders(): Observable<any> {
    return this.http.get(`${this.apiUrl}/my-history`);
  }

  getOrderById(orderId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${orderId}`);
  }

  updateOrderStatus(orderId: number, status: string): Observable<any> {
    const payload = { newStatus: status }; 
    return this.http.patch(`${this.apiUrl}/${orderId}/status`, payload); 
  }

  cancelOrder(orderId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${orderId}/cancel`, {});
  }

  // THE FIX: Logistics explicit cancellation API
  logisticsCancelOrder(orderId: number, reason: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${orderId}/logistics-cancel`, { reason });
  }
}