import { Component, OnInit } from '@angular/core';
import { AnalyticsService, EngagementReport, EngagementReportRequest } from '../../../core/services/analytics.service';

@Component({
  selector: 'app-analytics-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  reports: EngagementReport[] = [];
  loading = false;
  errorMsg = '';
  successMsg = '';

  // Form state
  showForm = false;
  editingReportId: number | null = null;
  formData: any = this.emptyForm();

  // Campaign trigger
  campaignCustomerId: number | null = null;

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.loadReports();
  }

  loadReports(): void {
    this.loading = true;
    this.errorMsg = '';
    this.analyticsService.getAllReports().subscribe({
      next: (data: EngagementReport[]) => {
        this.reports = data;
        this.loading = false;
      },
      error: (err: any) => {
        this.errorMsg = 'Failed to load reports. Is the backend running?';
        this.loading = false;
        console.error(err);
      }
    });
  }

  openCreateForm(): void {
    this.editingReportId = null;
    this.formData = this.emptyForm();
    this.showForm = true;
    this.successMsg = '';
  }

  openEditForm(report: EngagementReport): void {
    this.editingReportId = report.reportId!;
    this.formData = {
      customerId: report.customerId,
      abandonedCartCount: report.behaviorMetrics?.abandonedCartCount || 0,
      favouriteProduct: report.behaviorMetrics?.favouriteProduct || '',
      campaignName: report.campaignResponse?.campaignName || '',
      abandonedCartReminderSent: report.campaignResponse?.abandonedCartReminderSent || false,
      loyaltyPoints: report.campaignResponse?.loyaltyPoints || 0,
      responseStatus: report.campaignResponse?.responseStatus || 'SENT'
    };
    this.showForm = true;
    this.successMsg = '';
  }

  submitForm(): void {
    this.errorMsg = '';
    
    // Structure data properly for backend
    const payload: EngagementReportRequest = {
      customerId: this.formData.customerId,
      behaviorMetrics: {
        totalOrders: 0,
        repeatPurchaseCount: 0,
        abandonedCartCount: this.formData.abandonedCartCount,
        favouriteProduct: this.formData.favouriteProduct,
        averageOrderValue: 0
      },
      campaignResponse: {
        campaignName: this.formData.campaignName,
        loyaltyPoints: this.formData.loyaltyPoints,
        responseStatus: this.formData.responseStatus,
        abandonedCartReminderSent: this.formData.abandonedCartReminderSent
      }
    };

    if (this.editingReportId) {
      this.analyticsService.updateReport(this.editingReportId, payload).subscribe({
        next: () => {
          this.successMsg = 'Report updated successfully!';
          this.showForm = false;
          this.loadReports();
        },
        error: (err: any) => {
          this.errorMsg = 'Failed to update report.';
          console.error(err);
        }
      });
    } else {
      this.analyticsService.createReport(payload).subscribe({
        next: () => {
          this.successMsg = 'Report created successfully!';
          this.showForm = false;
          this.loadReports();
        },
        error: (err: any) => {
          this.errorMsg = 'Failed to create report.';
          console.error(err);
        }
      });
    }
  }

  deleteReport(reportId: number): void {
    if (confirm('Are you sure you want to delete this report?')) {
      this.analyticsService.deleteReport(reportId).subscribe({
        next: () => {
          this.successMsg = 'Report deleted!';
          this.loadReports();
        },
        error: (err: any) => {
          this.errorMsg = 'Failed to delete report.';
          console.error(err);
        }
      });
    }
  }

  triggerCampaign(): void {
    if (!this.campaignCustomerId) return;
    this.analyticsService.triggerAbandonedCartCampaign(this.campaignCustomerId).subscribe({
      next: (msg: string) => {
        this.successMsg = msg;
      },
      error: (err: any) => {
        this.errorMsg = 'Failed to trigger campaign.';
        console.error(err);
      }
    });
  }

  cancelForm(): void {
    this.showForm = false;
  }

  private emptyForm(): any {
    return {
      customerId: 0,
      abandonedCartCount: 0,
      favouriteProduct: '',
      campaignName: '',
      abandonedCartReminderSent: false,
      loyaltyPoints: 0,
      responseStatus: 'SENT'
    };
  }
}