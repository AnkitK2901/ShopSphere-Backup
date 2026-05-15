import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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

  getShipmentById(id: number): Observable<ShipmentResponse> {
    return this.http.get<ShipmentResponse>(`${this.apiUrl}/order/${id}`)
      .pipe(catchError(this.handleError));
  }

  getEnrichedShipmentByOrderId(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/enriched/order/${id}`)
      .pipe(catchError(this.handleError));
  }

  getAllShipments(): Observable<ShipmentResponse[]> {
    return this.http.get<ShipmentResponse[]>(this.apiUrl)
      .pipe(catchError(this.handleError));
  }

  updateStatus(orderId: string, newStatus: string, carrier?: string): Observable<ShipmentResponse> {
    let url = `${this.apiUrl}/order/${orderId}/${newStatus}`;
    if (carrier) {
      url += `?carrier=${encodeURIComponent(carrier)}`;
    }
    // THE FIX: Changed from .patch to .put to match the new backend controller
    return this.http.put<ShipmentResponse>(url, {})
      .pipe(catchError(this.handleError));
  }

  private handleError(error: any) {
    console.error('Logistics API Error:', error);
    return throwError(() => new Error('Failed to load the fulfillment queue. Check backend connection.'));
  }
}