import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EngagementReport {
  reportId?: number;
  customerId: number;
  behaviorMetrics: any;
  campaignResponse: any;
  createdAt?: string;
  updatedAt?: string;
}

// FIX: Added the missing interface the component expects
export interface EngagementReportRequest extends EngagementReport {}

@Injectable({
  providedIn: 'root',
})
export class AnalyticsService {
  private baseUrl = 'http://localhost:9090';

  constructor(private http: HttpClient) {}

  getAllReports(): Observable<EngagementReport[]> {
    return this.http.get<EngagementReport[]>(`${this.baseUrl}/api/analytics/reports`);
  }

  getReportById(reportId: number): Observable<EngagementReport> {
    return this.http.get<EngagementReport>(`${this.baseUrl}/api/analytics/reports/${reportId}`);
  }

  getReportsByCustomerId(customerId: number): Observable<EngagementReport[]> {
    return this.http.get<EngagementReport[]>(`${this.baseUrl}/api/analytics/reports/customer/${customerId}`);
  }

  // FIX: Restored missing CRUD endpoints expected by the Component
  createReport(report: EngagementReportRequest): Observable<EngagementReport> {
    return this.http.post<EngagementReport>(`${this.baseUrl}/api/analytics/reports`, report);
  }

  updateReport(reportId: number, report: EngagementReportRequest): Observable<EngagementReport> {
    return this.http.put<EngagementReport>(`${this.baseUrl}/api/analytics/reports/${reportId}`, report);
  }

  deleteReport(reportId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/api/analytics/reports/${reportId}`);
  }

  triggerAbandonedCartCampaign(customerId: number): Observable<string> {
    return this.http.post(`${this.baseUrl}/api/analytics/campaigns/trigger-abandoned-cart/${customerId}`, null, { responseType: 'text' });
  }
}