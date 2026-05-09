import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CatalogService {
  
  // FIX: Match the API Gateway's exact expected path prefix
  private apiUrl = 'http://localhost:9090/api';

  constructor(private http: HttpClient) {}

  // Includes Pagination and Search
  getProducts(page: number = 0, size: number = 10, search?: string): Observable<any> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    if (search) {
      params = params.set('keyword', search);
    }
    // Will correctly call: http://localhost:9090/api/products
    return this.http.get(`${this.apiUrl}/products`, { params });
  }

  getProductById(id: string | number): Observable<any> {
    // Will correctly call: http://localhost:9090/api/products/{id}
    return this.http.get(`${this.apiUrl}/products/${id}`);
  }

  // Fetch the custom variations (Color, Size, Material)
  getCustomOptions(): Observable<any> {
    // Will correctly call: http://localhost:9090/api/options (Matches your CustomOptionController)
    return this.http.get(`${this.apiUrl}/options`);
  }
}