import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ArtisanService } from '../../../core/services/artisan.service';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent {
  productForm: FormGroup;
  isSubmitting: boolean = false;

  constructor(
    private fb: FormBuilder,
    private artisanService: ArtisanService,
    private router: Router
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0)]],
      stockLevel: ['', [Validators.required, Validators.min(0)]],
      reorderThreshold: ['', [Validators.required, Validators.min(0)]],
      supplierId: ['', Validators.required],
      supplierLeadTimeDays: ['', [Validators.required, Validators.min(1)]],
      previewImageUrl: [''] // Added placeholder for text-based image URL
    });
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      this.isSubmitting = true;
      
      // FIX: Clean JSON payload sent instead of FormData
      const inventoryPayload = {
        name: this.productForm.value.name,
        description: this.productForm.value.description,
        basePrice: this.productForm.value.price,
        stockLevel: this.productForm.value.stockLevel,
        reorderThreshold: this.productForm.value.reorderThreshold,
        supplierId: this.productForm.value.supplierId,
        supplierLeadTimeDays: this.productForm.value.supplierLeadTimeDays,
        previewImage: this.productForm.value.previewImageUrl 
      };

      this.artisanService.addInventory(inventoryPayload).subscribe({
        next: (response: any) => {
          console.log('Product added successfully', response);
          this.isSubmitting = false;
          this.router.navigate(['/artisan/inventory']); 
        },
        error: (error: any) => {
          console.error('Error adding product:', error);
          this.isSubmitting = false;
        }
      });
    } else {
      this.productForm.markAllAsTouched();
    }
  }
}