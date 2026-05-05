import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
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

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    ArtisanDashboard,
    InventoryManagerComponent,
    ProductFormComponent,
    AnalyticsDashboard,
    CustomerInsightsComponent,
    RevenueReportComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,    
    FormsModule,         
    ReactiveFormsModule  
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }