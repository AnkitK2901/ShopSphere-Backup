import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InventoryItem { 
  productId: string; 
  stockLevel: number; 
  supplierId: string;
  reorderThreshold: number;
  supplierLeadTimeDays: number;
}

@Injectable({ providedIn: 'root' })
export class ArtisanService {
  // Pointing to the API Gateway
  private apiUrl = 'http://localhost:9090/api/inventory'; 

  constructor(private http: HttpClient) {}

  // This helper function automatically fetches the token from the browser's memory
  // It will grab whatever token your teammate's Login page saves!
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token') || ''; 
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  addInventory(item: InventoryItem): Observable<any> {
    return this.http.post(this.apiUrl, item, { 
      headers: this.getHeaders(), 
      responseType: 'text' 
    });
  }
}