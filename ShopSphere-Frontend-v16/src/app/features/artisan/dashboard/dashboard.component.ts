import { Component, OnInit } from '@angular/core';
import { ArtisanService, InventoryItem } from '../../../core/services/artisan.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  totalProducts: number = 0;
  totalItemsInStock: number = 0;
  errorMessage: string = ''; // Added error state

  constructor(private artisanService: ArtisanService) {}

  ngOnInit(): void {
    this.artisanService.getInventory().subscribe({
      next: (items: InventoryItem[]) => {
        this.totalProducts = items.length;
        this.totalItemsInStock = items.reduce((sum, item) => sum + item.stockLevel, 0);
      },
      error: (err: any) => {
        console.error('Backend connection failed:', err);
        this.errorMessage = 'Could not connect to backend or invalid token.';
      }
    });
  }
}