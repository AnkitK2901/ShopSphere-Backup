import { Component, OnInit } from '@angular/core';
import { ArtisanService } from '../../../core/services/artisan.service';
import { CatalogService } from '../../../core/services/catalog.service';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-inventory-manager',
  templateUrl: './inventory-manager.component.html',
  styleUrls: ['./inventory-manager.component.css']
})
export class InventoryManagerComponent implements OnInit {
  inventoryList: any[] = [];
  isLoading: boolean = true;

  constructor(
    private artisanService: ArtisanService,
    private catalogService: CatalogService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchInventoryAndEnrich();
  }

  fetchInventoryAndEnrich(): void {
    this.isLoading = true;
    this.artisanService.getInventory().subscribe({
      next: (inventoryData: any[]) => {
        // Create an array of requests to get product details for every item in inventory
        const enrichmentRequests = inventoryData.map(item => 
          this.catalogService.getProductById(item.productId)
        );

        if (enrichmentRequests.length === 0) {
          this.inventoryList = [];
          this.isLoading = false;
          return;
        }

        // Wait for all Catalog details to return
        forkJoin(enrichmentRequests).subscribe({
          next: (productDetails: any[]) => {
            // Merge Inventory data (stock) with Catalog data (name, image, price)
            this.inventoryList = inventoryData.map((invItem, index) => {
              const details = productDetails[index];
              return {
                ...invItem,
                name: details.name,
                basePrice: details.basePrice,
                // Handle both potential field names for images
                previewImage: details.previewImage || details.previewImageUrl 
              };
            });
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Failed to enrich inventory with catalog data', err);
            this.inventoryList = inventoryData; // Show stock even if names fail
            this.isLoading = false;
          }
        });
      },
      error: (error) => {
        console.error('Error fetching inventory:', error);
        this.isLoading = false;
      }
    });
  }

  editProduct(productId: number): void {
    // Navigates to the product form with the ID
    this.router.navigate(['/artisan/product/edit', productId]);
  }
}