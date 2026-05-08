import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CatalogService } from '../../../core/services/catalog.service';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css'] // Isolated CSS!
})
export class ProductDetailsComponent implements OnInit {
  product: any = null;
  customOptions: any[] = [];
  selectedOption: any = null;
  
  // State variables
  finalPrice: number = 0;
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private catalogService: CatalogService,
    private cartService: CartService // Connects to Fazil's Cart!
  ) {}

  ngOnInit(): void {
    // 1. Read the Product ID from the URL (e.g., /product/123)
    const productId = this.route.snapshot.paramMap.get('id');
    if (productId) {
      this.fetchProductDetails(productId);
    } else {
      this.errorMessage = "Product ID is missing.";
      this.isLoading = false;
    }
  }

  fetchProductDetails(id: string): void {
    this.catalogService.getProductById(id).subscribe({
      next: (prod) => {
        this.product = prod;
        this.finalPrice = prod.basePrice; // Set initial price
        
        // 2. Once we have the product, fetch its custom sizes/colors
        this.catalogService.getCustomOptions(id).subscribe({
          next: (options) => {
            this.customOptions = options;
            this.isLoading = false;
          },
          error: () => this.isLoading = false // It's okay if it has no options
        });
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = "Could not load product details.";
        this.isLoading = false;
      }
    });
  }

  // 3. The Interactive Builder Logic
  onOptionSelect(option: any): void {
    this.selectedOption = option;
    // Update the price dynamically based on the selection
    this.finalPrice = this.product.basePrice + (option.priceModifier || 0);
  }

  // 4. Send to Cart
  addToCart(): void {
    if (this.customOptions.length > 0 && !this.selectedOption) {
      alert("Please select an option (like Size or Color) before adding to cart.");
      return;
    }
    
    // Save to the memory state and redirect the user
    this.cartService.addToCart(this.product, this.selectedOption, this.finalPrice);
    this.router.navigate(['/cart']); 
  }
}