import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common'; 
import { ArtisanService, InventoryItem } from '../artisan';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {
  totalProducts: number = 0;
  totalItemsInStock: number = 0;

  constructor(private artisanService: ArtisanService) {}

  ngOnInit() {
    this.artisanService.getInventory().subscribe((items: InventoryItem[]) => {
      this.totalProducts = items.length;
      // Adds up all the stock levels from the database
      this.totalItemsInStock = items.reduce((sum, item) => sum + item.stockLevel, 0);
    });
  }
}