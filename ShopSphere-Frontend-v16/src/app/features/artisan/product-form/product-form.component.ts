import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ArtisanService } from '../../../core/services/artisan.service';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnInit {
  productForm: FormGroup;
  isSubmitting: boolean = false;
  availableOptions: any[] = [];

  constructor(
    private fb: FormBuilder,
    private artisanService: ArtisanService,
    private router: Router
  ) {
    this.productForm = this.fb.group({
      // Catalog fields
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0)]],
      previewImageUrl: [''],
      isActive: [true],
      selectedOptionIds: [[]],
      
      // Inventory fields
      stockLevel: ['', [Validators.required, Validators.min(0)]],
      reorderThreshold: [10, [Validators.required, Validators.min(0)]],
      supplierId: ['SELF', Validators.required],
      supplierLeadTimeDays: [7, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.loadOptions();
  }

  loadOptions(): void {
    this.artisanService.getAvailableOptions().subscribe({
      next: (res) => this.availableOptions = res,
      error: (err) => console.error("Could not load options", err)
    });
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      this.isSubmitting = true;
      const formVals = this.productForm.value;

      const catalogPayload = {
        name: formVals.name,
        description: formVals.description,
        basePrice: formVals.price,
        previewImage: formVals.previewImageUrl,
        isActive: formVals.isActive,
        selectedOptionIds: formVals.selectedOptionIds
      };

      this.artisanService.createProductAndInitializeInventory(catalogPayload, formVals.stockLevel)
        .subscribe({
          next: () => {
            this.isSubmitting = false;
            this.router.navigate(['/artisan/inventory']); 
          },
          error: (error: any) => {
            console.error('Error creating product:', error);
            this.isSubmitting = false;
          }
        });
    } else {
      this.productForm.markAllAsTouched();
    }
  }
}