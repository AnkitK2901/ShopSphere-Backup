import { Component } from '@angular/core';
import { AnalyticsService, EngagementReport } from '../../../core/services/analytics.service';

@Component({
  selector: 'app-customer-insights',
  templateUrl: './customer-insights.component.html',
  styleUrls: ['./customer-insights.component.css']
})
export class CustomerInsightsComponent {

  customerId: number | null = null;
  reports: EngagementReport[] = [];
  searched = false;
  loading = false;
  errorMsg = '';

  constructor(private analyticsService: AnalyticsService) {}

  search(): void {
    if (!this.customerId) {
      this.errorMsg = 'Please enter a Customer ID.';
      return;
    }

    this.loading = true;
    this.errorMsg = '';
    this.reports = [];
    this.searched = false;

    this.analyticsService.getReportsByCustomerId(this.customerId).subscribe({
      next: (data: EngagementReport[]) => {
        this.reports = data;
        this.searched = true;
        this.loading = false;
      },
      error: (err: any) => {
        if (err.status === 403) {
          this.errorMsg = 'Token expired. Please login again.';
        } else if (err.status === 0) {
          this.errorMsg = 'Cannot reach the server. Is the backend running?';
        } else {
          this.errorMsg = 'Failed to fetch reports. Status: ' + err.status;
        }
        this.searched = true;
        this.loading = false;
      }
    });
  }
}