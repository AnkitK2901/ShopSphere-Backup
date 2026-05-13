import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, switchMap } from 'rxjs';

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

  createProductAndInitializeInventory(catalogPayload: any, stockLevel: number): Observable<any> {
    // 1. Create the Product in the Catalog Service
    return this.http.post<any>(`${this.gatewayUrl}/products/create`, catalogPayload)
      .pipe(
        switchMap(savedProduct => {
           // 2. Take the new ID and initialize the Inventory Service
           const productId = savedProduct.productId;
           
           const params = new HttpParams()
             .set('productId', productId.toString())
             .set('stockLevel', stockLevel.toString());

           return this.http.post(`${this.gatewayUrl}/inventory/initialize`, null, { 
             params: params,
             responseType: 'text' 
           });
        })
      );
  }
}