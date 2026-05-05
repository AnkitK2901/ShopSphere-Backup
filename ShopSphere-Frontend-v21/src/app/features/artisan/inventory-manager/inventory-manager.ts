import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { ArtisanService, InventoryItem } from '../artisan';

@Component({
  selector: 'app-inventory-manager',
  imports: [CommonModule],
  templateUrl: './inventory-manager.html',
  styleUrl: './inventory-manager.scss',
})
export class InventoryManager implements OnInit {
  inventoryList: InventoryItem[] = [];

  constructor(private artisanService: ArtisanService) {}

  ngOnInit() {
    this.artisanService.getInventory().subscribe((data: InventoryItem[]) => {
      this.inventoryList = data;
    });
  }
}