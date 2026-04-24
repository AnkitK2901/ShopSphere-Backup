import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ArtisanService } from '../artisan';

@Component({
  selector: 'app-product-form',
  imports: [ReactiveFormsModule],
  templateUrl: './product-form.html',
  styleUrl: './product-form.scss'
})
export class ProductForm implements OnInit {
  inventoryForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private artisanService: ArtisanService,
    private router: Router
  ) {}

  ngOnInit() {
    // These perfectly match InventoryItem.java!
    this.inventoryForm = this.fb.group({
      productId: ['', Validators.required],
      stockLevel: [0, [Validators.required, Validators.min(0)]],
      supplierId: ['', Validators.required],
      reorderThreshold: [5, Validators.required],
      supplierLeadTimeDays: [7, Validators.required]
    });
  }

  onSubmit() {
    if (this.inventoryForm.valid) {
      this.artisanService.addInventory(this.inventoryForm.value).subscribe({
        next: (response) => {
          alert('Inventory Added to Database Successfully!');
          this.router.navigate(['/artisan/inventory']); 
        },
        error: (err) => {
          alert('Failed to connect to backend! Make sure Spring Boot is running on port 8080.');
        }
      });
    }
  }
}