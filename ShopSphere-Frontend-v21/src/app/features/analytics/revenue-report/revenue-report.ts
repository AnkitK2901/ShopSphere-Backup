import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Analytics } from '../services/analytics';
import { EngagementReport } from '../models/analytics.models';

@Component({
  selector: 'app-revenue-report',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './revenue-report.html',
  styleUrl: './revenue-report.scss'
})
export class RevenueReport implements OnInit {

  reports: EngagementReport[] = [];
  loading = false;
  errorMsg = '';

  totalCustomers = 0;
  totalOrders = 0;
  avgOrderValue = 0;
  totalLoyaltyPoints = 0;
  totalAbandonedCarts = 0;
  convertedCampaigns = 0;
  ignoredCampaigns = 0;

  constructor(private analyticsService: Analytics, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadSummary();
  }

  loadSummary(): void {
    this.loading = true;
    this.analyticsService.getAllReports().subscribe({
      next: (data) => {
        this.reports = data;
        this.calculateStats();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMsg = 'Failed to load reports.';
        this.loading = false;
        this.cdr.detectChanges();
        console.error(err);
      }
    });
  }

  private calculateStats(): void {
    this.totalCustomers = new Set(this.reports.map(r => r.customerId)).size;
    this.totalOrders = this.reports.reduce((sum, r) => sum + (r.behaviorMetrics?.totalOrders || 0), 0);
    this.totalAbandonedCarts = this.reports.reduce((sum, r) => sum + (r.behaviorMetrics?.abandonedCartCount || 0), 0);
    this.totalLoyaltyPoints = this.reports.reduce((sum, r) => sum + (r.campaignResponse?.loyaltyPoints || 0), 0);
    this.convertedCampaigns = this.reports.filter(r => r.campaignResponse?.responseStatus === 'CONVERTED').length;
    this.ignoredCampaigns = this.reports.filter(r => r.campaignResponse?.responseStatus === 'IGNORED').length;

    const orderValues = this.reports
      .map(r => r.behaviorMetrics?.averageOrderValue || 0)
      .filter(v => v > 0);
    this.avgOrderValue = orderValues.length > 0
      ? orderValues.reduce((a, b) => a + b, 0) / orderValues.length
      : 0;
  }
}