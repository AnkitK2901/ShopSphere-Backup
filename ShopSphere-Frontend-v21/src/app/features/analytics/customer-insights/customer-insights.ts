import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Analytics } from '../services/analytics';
import { EngagementReport } from '../models/analytics.models';

@Component({
  selector: 'app-customer-insights',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customer-insights.html',
  styleUrl: './customer-insights.scss'
})
export class CustomerInsights {

  customerId: number | null = null;
  reports: EngagementReport[] = [];
  searched = false;
  loading = false;
  errorMsg = '';

  constructor(private analyticsService: Analytics) {}

  search(): void {
    if (!this.customerId) {
      this.errorMsg = 'Please enter a Customer ID.';
      return;
    }

    this.loading = true;
    this.errorMsg = '';
    this.reports = [];
    this.searched = false;

    console.log('Searching for customer:', this.customerId);

    this.analyticsService.getReportsByCustomerId(this.customerId).subscribe({
      next: (data) => {
        console.log('Data received:', data);
        this.reports = data;
        this.searched = true;
        this.loading = false;
      },
      error: (err) => {
        console.error('API Error:', err);
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

  // Allow pressing Enter to search
  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.search();
    }
  }
}