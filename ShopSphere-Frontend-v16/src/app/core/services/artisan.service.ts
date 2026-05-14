import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InventoryItem { 
  productId?: number; 
  stockLevel: number; 
  supplierId: string;
  reorderThreshold: number;
  supplierLeadTimeDays: number;
}

@Injectable({ providedIn: 'root' })
export class ArtisanService {
  private gatewayUrl = 'http://localhost:9090/api'; 

  constructor(private http: HttpClient) {}

  getInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(`${this.gatewayUrl}/inventory`);
  }

  getAvailableOptions(): Observable<any> {
    return this.http.get(`${this.gatewayUrl}/options`);
  }

  createProduct(catalogPayload: any): Observable<any> {
    return this.http.post<any>(`${this.gatewayUrl}/products/create`, catalogPayload);
  }
}