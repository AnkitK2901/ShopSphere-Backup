import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Auth Components
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { ProfileComponent } from './features/auth/profile/profile.component';

// Storefront Components
import { HomeComponent } from './features/storefront/home/home.component';
import { CatalogComponent } from './features/storefront/catalog/catalog.component';
import { ProductDetailsComponent } from './features/storefront/product-details/product-details.component';

// Order Components
import { CartComponent } from './features/orders/cart/cart.component';
import { CheckoutComponent } from './features/orders/checkout/checkout.component';
import { HistoryComponent } from './features/orders/history/history.component';

// Artisan Components
import { DashboardComponent as ArtisanDashboard } from './features/artisan/dashboard/dashboard.component';
import { InventoryManagerComponent } from './features/artisan/inventory-manager/inventory-manager.component';
import { ProductFormComponent } from './features/artisan/product-form/product-form.component';

// Logistics Components
import { QueueComponent } from './features/logistics/queue/queue.component';
import { PackingComponent } from './features/logistics/packing/packing.component';
import { DispatchComponent } from './features/logistics/dispatch/dispatch.component';
import { MonitorComponent } from './features/logistics/monitor/monitor.component';

// Analytics Components
import { DashboardComponent as AdminDashboard } from './features/analytics/dashboard/dashboard.component';
import { CustomerInsightsComponent } from './features/analytics/customer-insights/customer-insights.component';

import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';

const routes: Routes = [
  // Storefront (Public)
  { path: '', component: HomeComponent },
  { path: 'catalog', component: CatalogComponent },
  { path: 'product/:id', component: ProductDetailsComponent },

  // Auth (Public & Protected)
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },

  // Orders (Customer Only)
  { path: 'cart', component: CartComponent },
  { path: 'checkout', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'history', component: HistoryComponent, canActivate: [AuthGuard] },

  // Artisan / Seller routes (Protected)
  {
    path: 'artisan/dashboard',
    component: ArtisanDashboard,
    canActivate: [RoleGuard],
    data: { expectedRole: 'ROLE_ARTISAN' },
  },
  {
    path: 'artisan/inventory',
    component: InventoryManagerComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'ROLE_ARTISAN' },
  },
  {
    path: 'artisan/product/new',
    component: ProductFormComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'ROLE_ARTISAN' },
  },
  // FIX: Added the Edit route so the Inventory Manager "Edit" button actually works
  {
    path: 'artisan/product/edit/:id',
    component: ProductFormComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'ROLE_ARTISAN' },
  },

  // Logistics / Warehouse routes (Protected)
  {
    path: 'logistics/queue',
    component: QueueComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'ROLE_LOGISTICS' },
  },
  {
    path: 'logistics/packing/:id',
    component: PackingComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'ROLE_LOGISTICS' },
  },
  {
    path: 'logistics/dispatch/:id',
    component: DispatchComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'ROLE_LOGISTICS' },
  },
  {
    path: 'logistics/monitor',
    component: MonitorComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'ROLE_LOGISTICS' },
  },

  // Admin / Analytics routes (Protected)
  {
    path: 'admin/dashboard',
    component: AdminDashboard,
    canActivate: [RoleGuard],
    data: { expectedRole: 'ROLE_ADMIN' },
  },
  {
    path: 'admin/insights',
    component: CustomerInsightsComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'ROLE_ADMIN' },
  },

  // Fallback
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
