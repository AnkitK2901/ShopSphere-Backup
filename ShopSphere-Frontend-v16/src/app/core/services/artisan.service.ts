import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

// FIX: Expanded the interface to include the form fields and made ID optional
export interface InventoryItem { 
  productId?: string; 
  name?: string;
  description?: string;
  basePrice?: number;
  previewImage?: string;
  stockLevel: number; 
  supplierId: string;
  reorderThreshold: number;
  supplierLeadTimeDays: number;
}

@Injectable({ providedIn: 'root' })
export class ArtisanService {
  private apiUrl = 'http://localhost:9090/api/inventory'; 

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token') || ''; 
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  // FIX: Parameter set to 'any' to bypass strict Type checking during form submission
  addInventory(item: any): Observable<any> {
    return this.http.post(this.apiUrl, item, { 
      headers: this.getHeaders(), 
      responseType: 'text' 
    });
  }
}