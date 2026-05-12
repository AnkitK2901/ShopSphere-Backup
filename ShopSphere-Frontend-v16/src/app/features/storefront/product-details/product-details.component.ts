import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CatalogService } from '../../../core/services/catalog.service';
import { CartService } from '../../../core/services/cart.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css']
})
export class ProductDetailsComponent implements OnInit {
  product: any = null;
  isLoading: boolean = true;
  selectedQuantity: number = 1;
  
  errorMessage: string = '';
  finalPrice: number = 0;
  customOptions: any[] = [];
  selectedOption: any = null;

  constructor(
    private route: ActivatedRoute,
    private catalogService: CatalogService,
    private cartService: CartService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.fetchProductDetails(id);
    } else {
      this.router.navigate(['/catalog']);
    }
  }

  fetchProductDetails(id: string): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.catalogService.getProductById(id).subscribe({
      next: (data) => {
        this.product = data;
        this.finalPrice = data.basePrice;
        this.customOptions = data.customOptions || [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load product', err);
        this.errorMessage = 'Could not load product details. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  onOptionSelect(opt: any): void {
    this.selectedOption = opt;
    this.finalPrice = this.product.basePrice + (opt.priceAdjustment || 0);
  }

  increaseQuantity(): void {
    this.selectedQuantity++;
  }

  decreaseQuantity(): void {
    if (this.selectedQuantity > 1) {
      this.selectedQuantity--;
    }
  }

  addToCart(): void {
    if (this.product) {
      // THE FIX: Check if the product has variations and ensure one is selected
      if (this.customOptions.length > 0 && !this.selectedOption) {
        this.toastService.showError('Please select an option before adding to cart.');
        return;
      }

      const cartItem = {
        ...this.product,
        // Ensure finalPrice is used for calculation
        basePrice: this.finalPrice, 
        // Explicitly map the selected choice
        selectedOption: this.selectedOption
      };
      
      this.cartService.addToCart(cartItem, this.selectedQuantity);
      this.toastService.showSuccess(`Added ${this.selectedQuantity}x ${this.product.name} to your cart.`);
    }
  }
}