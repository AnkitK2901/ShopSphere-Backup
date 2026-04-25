import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EngagementReport, EngagementReportRequest } from '../models/analytics.models';

@Injectable({
  providedIn: 'root',
})
export class Analytics {

  private baseUrl = 'http://localhost:9090';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
      'X-User-Role': 'ROLE_ADMIN'
    });
  }

  getAllReports(): Observable<EngagementReport[]> {
    return this.http.get<EngagementReport[]>(
      `${this.baseUrl}/api/analytics/reports`,
      { headers: this.getHeaders() }
    );
  }

  getReportById(reportId: number): Observable<EngagementReport> {
    return this.http.get<EngagementReport>(
      `${this.baseUrl}/api/analytics/reports/${reportId}`,
      { headers: this.getHeaders() }
    );
  }

  getReportsByCustomerId(customerId: number): Observable<EngagementReport[]> {
    return this.http.get<EngagementReport[]>(
      `${this.baseUrl}/api/analytics/reports/customer/${customerId}`,
      { headers: this.getHeaders() }
    );
  }

  createReport(report: EngagementReportRequest): Observable<EngagementReport> {
    return this.http.post<EngagementReport>(
      `${this.baseUrl}/api/analytics/reports`,
      report,
      { headers: this.getHeaders() }
    );
  }

  updateReport(reportId: number, report: EngagementReportRequest): Observable<EngagementReport> {
    return this.http.put<EngagementReport>(
      `${this.baseUrl}/api/analytics/reports/${reportId}`,
      report,
      { headers: this.getHeaders() }
    );
  }

  deleteReport(reportId: number): Observable<any> {
    return this.http.delete(
      `${this.baseUrl}/api/analytics/reports/${reportId}`,
      { headers: this.getHeaders() }
    );
  }

  triggerAbandonedCartCampaign(customerId: number): Observable<string> {
    return this.http.post(
      `${this.baseUrl}/api/analytics/campaigns/trigger-abandoned-cart/${customerId}`,
      null,
      { headers: this.getHeaders(), responseType: 'text' }
    );
  }
}