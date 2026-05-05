import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent as ArtisanDashboard } from './features/artisan/dashboard/dashboard.component';
import { InventoryManagerComponent } from './features/artisan/inventory-manager/inventory-manager.component';
import { ProductFormComponent } from './features/artisan/product-form/product-form.component';
import { DashboardComponent as AnalyticsDashboard } from './features/analytics/dashboard/dashboard.component';
import { CustomerInsightsComponent } from './features/analytics/customer-insights/customer-insights.component';
import { RevenueReportComponent } from './features/analytics/revenue-report/revenue-report.component';

const routes: Routes = [
  { path: 'artisan/dashboard', component: ArtisanDashboard },
  { path: 'artisan/products/new', component: ProductFormComponent },
  { path: 'artisan/inventory', component: InventoryManagerComponent },

  { path: 'analytics/dashboard', component: AnalyticsDashboard },
  { path: 'analytics/customer-insights', component: CustomerInsightsComponent },
  { path: 'analytics/revenue-report', component: RevenueReportComponent },

  { path: '', redirectTo: 'artisan/dashboard', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }