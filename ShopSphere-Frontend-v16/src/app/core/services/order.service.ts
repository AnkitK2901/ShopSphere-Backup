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

  // FIX: Completely secured. The API Gateway applies the JWT token,
  // and the Backend controller figures out who the user is.
  getMyOrders(): Observable<any> {
    return this.http.get(`${this.apiUrl}/my-history`);
  }

  // ADDED MISSING METHOD:
  getOrderById(orderId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${orderId}`);
  }

  updateOrderStatus(orderId: number, status: string): Observable<any> {
    const payload = { newStatus: status }; 
    return this.http.patch(`${this.apiUrl}/${orderId}/status`, payload); 
  }
}