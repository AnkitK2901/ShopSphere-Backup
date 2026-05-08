import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:9090/order';

  constructor(private http: HttpClient, private authService: AuthService) {}

  placeOrder(orderData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, orderData);
  }

  // Decodes the JWT token to get the user's email/ID, then fetches their specific orders
  getMyOrders(): Observable<any> {
    // Note: Assuming your JWT payload uses 'sub' for the user identifier
    const token = localStorage.getItem('jwt_token') || '';
    const payload = JSON.parse(atob(token.split('.')[1]));
    const userId = payload.sub; 
    
    return this.http.get(`${this.apiUrl}/user/${userId}`);
  }
  // Add this inside OrderService class
  updateOrderStatus(orderId: number, status: string): Observable<any> {
    const payload = { status: status };
    // Adjust the URL if your Spring Boot endpoint is different
    return this.http.put(`${this.apiUrl}/${orderId}/status`, payload); 
  }
}