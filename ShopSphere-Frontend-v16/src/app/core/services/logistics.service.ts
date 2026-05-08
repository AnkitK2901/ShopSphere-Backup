import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LogisticsService {
  private apiUrl = 'http://localhost:9090/logistics';

  constructor(private http: HttpClient) {}

  // Fetch orders based on their current status
  getShipmentsByStatus(status: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/shipments?status=${status}`);
  }

  // Fetch all shipments for the Global Monitor
  getAllShipments(): Observable<any> {
    return this.http.get(`${this.apiUrl}/shipments/all`);
  }

  // Get a single shipment for packing details
  getShipmentById(id: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/shipments/${id}`);
  }

  // Update status (e.g., CONFIRMED -> PACKED)
  updateStatus(id: string, newStatus: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/shipments/${id}/status`, { status: newStatus });
  }

  // Assign courier and dispatch
  dispatchShipment(id: string, dispatchData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/shipments/${id}/dispatch`, dispatchData);
  }
}