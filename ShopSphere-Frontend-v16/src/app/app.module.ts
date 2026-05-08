import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { DashboardComponent as ArtisanDashboard } from './features/artisan/dashboard/dashboard.component';
import { InventoryManagerComponent } from './features/artisan/inventory-manager/inventory-manager.component';
import { ProductFormComponent } from './features/artisan/product-form/product-form.component';
import { DashboardComponent as AnalyticsDashboard } from './features/analytics/dashboard/dashboard.component';
import { CustomerInsightsComponent } from './features/analytics/customer-insights/customer-insights.component';
import { RevenueReportComponent } from './features/analytics/revenue-report/revenue-report.component';

import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { HomeComponent } from './features/storefront/home/home.component';
import { CatalogComponent } from './features/storefront/catalog/catalog.component';
import { ProductDetailsComponent } from './features/storefront/product-details/product-details.component';
import { CartComponent } from './features/orders/cart/cart.component';
import { CheckoutComponent } from './features/orders/checkout/checkout.component';
import { HistoryComponent } from './features/orders/history/history.component';
import { QueueComponent } from './features/logistics/queue/queue.component';
import { PackingComponent } from './features/logistics/packing/packing.component';
import { DispatchComponent } from './features/logistics/dispatch/dispatch.component';
import { MonitorComponent } from './features/logistics/monitor/monitor.component';
import { CommonModule } from '@angular/common';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    ArtisanDashboard,
    InventoryManagerComponent,
    ProductFormComponent,
    AnalyticsDashboard,
    CustomerInsightsComponent,
    RevenueReportComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    CatalogComponent,
    ProductDetailsComponent,
    CartComponent,
    CheckoutComponent,
    HistoryComponent,
    QueueComponent,
    PackingComponent,
    DispatchComponent,
    MonitorComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
