import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface ShipmentResponse {
  shipmentId: number;
  orderId: number;
  trackingNumber: string;
  trackingUrl: string;
  status: string;
  carrier: string;
  updatedAt: string;
}

@Injectable({ providedIn: 'root' })
export class LogisticsService {
  
  private apiUrl = 'http://localhost:9090/api/shipments';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token') || ''; 
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getShipmentById(id: number): Observable<ShipmentResponse> {
    return this.http.get<ShipmentResponse>(`${this.apiUrl}/order/${id}`, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  getAllShipments(): Observable<ShipmentResponse[]> {
    // FIX: Removed /all. Calling base URL matches backend GET endpoint
    return this.http.get<ShipmentResponse[]>(this.apiUrl, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  updateStatus(orderId: number, newStatus: string): Observable<ShipmentResponse> {
    return this.http.patch<ShipmentResponse>(`${this.apiUrl}/order/${orderId}/${newStatus}`, {}, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  private handleError(error: any) {
    console.error('Logistics API Error:', error);
    return throwError(() => new Error('Failed to load the fulfillment queue. Check backend connection.'));
  }
}