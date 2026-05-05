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
      supplierLeadTimeDays: ['', [Validators.required, Validators.min(1)]]
    });
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      this.isSubmitting = true;
      
      // FIX: Changed from addProduct to addInventory to match your v21 service
      this.artisanService.addInventory(this.productForm.value).subscribe({
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