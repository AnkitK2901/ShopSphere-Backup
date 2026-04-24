import { Routes } from '@angular/router';
import { Dashboard } from './features/artisan/dashboard/dashboard';
import { ProductForm } from './features/artisan/product-form/product-form';
import { InventoryManager } from './features/artisan/inventory-manager/inventory-manager';

export const routes: Routes = [
  { path: 'artisan/dashboard', component: Dashboard },
  { path: 'artisan/products/new', component: ProductForm },
  { path: 'artisan/inventory', component: InventoryManager },
  
  { path: '', redirectTo: 'artisan/dashboard', pathMatch: 'full' }
];