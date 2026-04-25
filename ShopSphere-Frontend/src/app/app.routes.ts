import { Routes } from '@angular/router';
import { Dashboard } from './features/artisan/dashboard/dashboard';
import { ProductForm } from './features/artisan/product-form/product-form';
import { InventoryManager } from './features/artisan/inventory-manager/inventory-manager';
import { Dashboard as AnalyticsDashboard } from './features/analytics/dashboard/dashboard';
import { CustomerInsights } from './features/analytics/customer-insights/customer-insights';
import { RevenueReport } from './features/analytics/revenue-report/revenue-report';

export const routes: Routes = [
  { path: 'artisan/dashboard', component: Dashboard },
  { path: 'artisan/products/new', component: ProductForm },
  { path: 'artisan/inventory', component: InventoryManager },

  { path: 'analytics/dashboard', component: AnalyticsDashboard },
  { path: 'analytics/customer-insights', component: CustomerInsights },
  { path: 'analytics/revenue-report', component: RevenueReport },

  { path: '', redirectTo: 'artisan/dashboard', pathMatch: 'full' }
];