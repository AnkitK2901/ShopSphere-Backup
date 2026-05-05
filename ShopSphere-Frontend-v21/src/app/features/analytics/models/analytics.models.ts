export interface BehaviorMetrics {
  metricsId?: number;
  totalOrders: number;
  repeatPurchaseCount: number;
  abandonedCartCount: number;
  favouriteProduct: string;
  averageOrderValue: number;
}

export interface CampaignResponse {
  campaignResponseId?: number;
  campaignName: string;
  abandonedCartReminderSent: boolean;
  loyaltyPoints: number;
  responseStatus: 'SENT' | 'OPENED' | 'CLICKED' | 'CONVERTED' | 'IGNORED';
}

export interface EngagementReport {
  reportId?: number;
  customerId: number;
  behaviorMetrics: BehaviorMetrics;
  campaignResponse: CampaignResponse;
  createdAt?: string;
  updatedAt?: string;
}

export interface EngagementReportRequest {
  customerId: number;
  abandonedCartCount: number;
  favouriteProduct: string;
  campaignName: string;
  abandonedCartReminderSent: boolean;
  loyaltyPoints: number;
  responseStatus: 'SENT' | 'OPENED' | 'CLICKED' | 'CONVERTED' | 'IGNORED';
}