import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Auth
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard'; // FIX: Imported RoleGuard

// Artisan
import { DashboardComponent as ArtisanDashboard } from './features/artisan/dashboard/dashboard.component';
import { InventoryManagerComponent } from './features/artisan/inventory-manager/inventory-manager.component';
import { ProductFormComponent } from './features/artisan/product-form/product-form.component';

// Analytics
import { DashboardComponent as AnalyticsDashboard } from './features/analytics/dashboard/dashboard.component';
import { CustomerInsightsComponent } from './features/analytics/customer-insights/customer-insights.component';
import { RevenueReportComponent } from './features/analytics/revenue-report/revenue-report.component';

// Storefront & Orders
import { HomeComponent } from './features/storefront/home/home.component';
import { CatalogComponent } from './features/storefront/catalog/catalog.component';
import { ProductDetailsComponent } from './features/storefront/product-details/product-details.component';
import { CartComponent } from './features/orders/cart/cart.component';
import { CheckoutComponent } from './features/orders/checkout/checkout.component';
import { HistoryComponent } from './features/orders/history/history.component';

// Logistics
import { QueueComponent } from './features/logistics/queue/queue.component';
import { PackingComponent } from './features/logistics/packing/packing.component';
import { DispatchComponent } from './features/logistics/dispatch/dispatch.component';
import { MonitorComponent } from './features/logistics/monitor/monitor.component';

const routes: Routes = [
  // Public Routes
  { path: '', component: CatalogComponent },
  { path: 'catalog', redirectTo: '', pathMatch: 'full' },
  { path: 'product/:id', component: ProductDetailsComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // Customer Routes (Requires simple Auth)
  { path: 'cart', component: CartComponent },
  { path: 'checkout', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'orders/history', component: HistoryComponent, canActivate: [AuthGuard] },

  // FIX: Applied strictly enforced Role Guards to Dashboards!
  { path: 'artisan/dashboard', component: ArtisanDashboard, canActivate: [RoleGuard], data: { expectedRole: 'ROLE_ARTISAN' } },
  { path: 'artisan/products/new', component: ProductFormComponent, canActivate: [RoleGuard], data: { expectedRole: 'ROLE_ARTISAN' } },
  { path: 'artisan/inventory', component: InventoryManagerComponent, canActivate: [RoleGuard], data: { expectedRole: 'ROLE_ARTISAN' } },

  { path: 'analytics/dashboard', component: AnalyticsDashboard, canActivate: [RoleGuard], data: { expectedRole: 'ROLE_ADMIN' } },
  { path: 'analytics/customer-insights', component: CustomerInsightsComponent, canActivate: [RoleGuard], data: { expectedRole: 'ROLE_ADMIN' } },
  { path: 'analytics/revenue-report', component: RevenueReportComponent, canActivate: [RoleGuard], data: { expectedRole: 'ROLE_ADMIN' } },

  { path: 'logistics/queue', component: QueueComponent, canActivate: [RoleGuard], data: { expectedRole: 'ROLE_LOGISTICS' } },
  { path: 'logistics/pack/:id', component: PackingComponent, canActivate: [RoleGuard], data: { expectedRole: 'ROLE_LOGISTICS' } },
  { path: 'logistics/dispatch/:id', component: DispatchComponent, canActivate: [RoleGuard], data: { expectedRole: 'ROLE_LOGISTICS' } },
  { path: 'logistics/monitor', component: MonitorComponent, canActivate: [RoleGuard], data: { expectedRole: 'ROLE_LOGISTICS' } },

  // Fallback
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }