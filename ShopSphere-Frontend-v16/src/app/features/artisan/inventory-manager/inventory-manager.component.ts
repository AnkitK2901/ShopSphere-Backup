import { Component, OnInit } from '@angular/core';
import { ArtisanService } from '../../../core/services/artisan.service';

@Component({
  selector: 'app-inventory-manager',
  templateUrl: './inventory-manager.component.html',
  styleUrls: ['./inventory-manager.component.css']
})
export class InventoryManagerComponent implements OnInit {
  inventoryList: any[] = [];
  isLoading: boolean = true;

  constructor(private artisanService: ArtisanService) {}

  ngOnInit(): void {
    this.fetchInventory();
  }

  fetchInventory(): void {
    this.artisanService.getInventory().subscribe({
      // Added strict typing: (data: any[]) and (error: any)
      next: (data: any[]) => {
        this.inventoryList = data;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error fetching inventory from Spring Boot backend:', error);
        this.isLoading = false;
      }
    });
  }
}