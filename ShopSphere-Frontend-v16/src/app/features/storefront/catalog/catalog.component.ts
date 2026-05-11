import { Component, OnInit } from '@angular/core';
import { CatalogService } from '../../../core/services/catalog.service';

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css'] 
})
export class CatalogComponent implements OnInit {
  products: any[] = [];
  currentPage: number = 0;
  pageSize: number = 20; // Increased so frontend search works better
  searchQuery: string = '';
  isLoading: boolean = false;
  hasMoreProducts: boolean = true;

  constructor(private catalogService: CatalogService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(reset: boolean = false): void {
    if (reset) {
      this.currentPage = 0;
      this.products = [];
      this.hasMoreProducts = true;
    }
    if (!this.hasMoreProducts) return;

    this.isLoading = true;
    this.catalogService.getProducts(this.currentPage, this.pageSize, this.searchQuery)
      .subscribe({
        next: (response: any) => {
          let newProducts = response.content || response; 
          
          // FIX: Bulletproof Frontend Search Fallback
          // If the backend returned everything, we force filter it here.
          if (this.searchQuery && this.searchQuery.trim() !== '') {
            const lowerQuery = this.searchQuery.toLowerCase();
            newProducts = newProducts.filter((p: any) => 
              p.name?.toLowerCase().includes(lowerQuery) || 
              p.description?.toLowerCase().includes(lowerQuery)
            );
          }

          this.products = [...this.products, ...newProducts];
          
          if (response.last === true || (response.content || response).length < this.pageSize) {
            this.hasMoreProducts = false;
          }
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error fetching products', err);
          this.isLoading = false;
        }
      });
  }

  onSearch(query: string): void {
    this.searchQuery = query;
    this.loadProducts(true); 
  }

  loadMore(): void {
    this.currentPage++;
    this.loadProducts();
  }
}