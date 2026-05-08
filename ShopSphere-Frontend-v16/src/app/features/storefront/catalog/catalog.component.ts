import { Component, OnInit } from '@angular/core';
import { CatalogService } from '../../../core/services/catalog.service';

@Component({
  selector: 'app-catalog',
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css'] // Isolated CSS!
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
    this.catalogService.getProducts(this.currentPage, this.pageSize, this.searchQuery)
      .subscribe({
        next: (response: any) => {
          // Extracts the array from Spring Boot's Page<T> object
          const newProducts = response.content || response; 
          this.products = [...this.products, ...newProducts];
          
          // If we got fewer products back than we asked for, we hit the end of the database
          if (newProducts.length < this.pageSize) {
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

  onSearch(): void {
    this.loadProducts(true); // Resets the grid and searches from page 0
  }

  loadMore(): void {
    this.currentPage++;
    this.loadProducts();
  }
}