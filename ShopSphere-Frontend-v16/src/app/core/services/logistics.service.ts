import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LogisticsService {
  // FIX: Pointing strictly to the API Gateway route
  private apiUrl = 'http://localhost:9090/api/shipments';

  constructor(private http: HttpClient) {}

  getAllShipments(): Observable<any> {
    // Matches Backend: @GetMapping ""
    return this.http.get(`${this.apiUrl}`);
  }

  getShipmentById(orderId: string): Observable<any> {
    // Matches Backend: @GetMapping("/order/{orderId}")
    return this.http.get(`${this.apiUrl}/order/${orderId}`);
  }

  updateStatus(orderId: string, newStatus: string): Observable<any> {
    // Matches Backend: @PatchMapping("/order/{orderId}/{status}")
    return this.http.patch(`${this.apiUrl}/order/${orderId}/${newStatus}`, {});
  }
}