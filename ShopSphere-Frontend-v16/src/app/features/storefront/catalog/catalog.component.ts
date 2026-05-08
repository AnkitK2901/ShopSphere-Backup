import { Component, OnInit } from '@angular/core';
import { CatalogService } from '../../../core/services/catalog.service';

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css'], // Isolated CSS!
})
export class CatalogComponent implements OnInit {
  products: any[] = [];
  currentPage: number = 0;
  pageSize: number = 8;
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
    this.catalogService
      .getProducts(this.currentPage, this.pageSize, this.searchQuery)
      .subscribe({
        next: (response: any) => {
          const newProducts = response.content || response;

          // Optional: Sanitize or provide defaults if backend fields are missing
          const sanitizedProducts = newProducts.map((p: any) => ({
            ...p,
            description: p.description || 'No description provided.',
            // Ensure we have a valid ID for routing even if name varies
            id: p.productId || p.id,
          }));
``
          this.products = [...this.products, ...sanitizedProducts];

          if (sanitizedProducts.length < this.pageSize) {
            this.hasMoreProducts = false;
          }
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error fetching products', err);
          this.isLoading = false;
        },
      });
  }

  onSearch(): void {
    this.loadProducts(true); // Resets the grid and searches from page 0
  }

  loadMore(): void {
    this.currentPage++;
    this.loadProducts();
  }
}
