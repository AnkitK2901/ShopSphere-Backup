import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CatalogService {
  // Routes through the API Gateway
  private apiUrl = 'http://localhost:9090/catalog/products';

  constructor(private http: HttpClient) {}

  // Includes Pagination and Search
  getProducts(page: number = 0, size: number = 10, search?: string): Observable<any> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    if (search) {
      params = params.set('keyword', search);
    }
    return this.http.get(`${this.apiUrl}/products`, { params });
  }

  getProductById(id: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/products/${id}`);
  }

  // Fetch the custom variations (Color, Size, Material)
  getCustomOptions(productId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/custom-options/product/${productId}`);
  }
}