package com.shopsphere.analytics_service.Service.Impl;

import com.shopsphere.analytics_service.Client.AuthFeignClient;
import com.shopsphere.analytics_service.Client.AuthFeignClient.UserResponse;
import com.shopsphere.analytics_service.Client.CatalogFeignClient;
import com.shopsphere.analytics_service.Client.CatalogFeignClient.ProductResponse;
import com.shopsphere.analytics_service.Client.OrderFeignClient;
import com.shopsphere.analytics_service.Client.OrderFeignClient.OrderResponse;
import com.shopsphere.analytics_service.DTO.EngagementReportRequest;
import com.shopsphere.analytics_service.DTO.EngagementReportResponse;
import com.shopsphere.analytics_service.Entity.BehaviorMetricsEntity;
import com.shopsphere.analytics_service.Entity.CampaignResponseEntity;
import com.shopsphere.analytics_service.Entity.EngagementReportEntity;
import com.shopsphere.analytics_service.Enums.CampaignResponseStatus;
import com.shopsphere.analytics_service.Exception.ResourceNotFoundException;
import com.shopsphere.analytics_service.Repository.EngagementReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EngagementReportServiceImplTest {

        @Mock
        private EngagementReportRepository engagementReportRepository;

        @Mock
        private OrderFeignClient orderFeignClient;

        @Mock
        private AuthFeignClient authFeignClient;

        @Mock
        private CatalogFeignClient catalogFeignClient;

        @InjectMocks
        private EngagementReportServiceImpl service;

        // ── Shared test fixtures ──────────────────────────────────────────────────

        private EngagementReportEntity sampleEntity;
        private EngagementReportRequest sampleRequest;

        @BeforeEach
        void setUp() {
                // Build a reusable entity
                BehaviorMetricsEntity metrics = new BehaviorMetricsEntity();
                metrics.setMetricsId(1L);
                metrics.setTotalOrders(5);
                metrics.setRepeatPurchaseCount(4);
                metrics.setAbandonedCartCount(2);
                metrics.setFavouriteProduct("Wireless Mouse");
                metrics.setAverageOrderValue(250.0);

                CampaignResponseEntity campaign = new CampaignResponseEntity();
                campaign.setCampaignResponseId(1L);
                campaign.setCampaignName("Summer Sale");
                campaign.setAbandonedCartReminderSent(true);
                campaign.setLoyaltyPoints(100);
                campaign.setResponseStatus(CampaignResponseStatus.CLICKED);

                sampleEntity = new EngagementReportEntity();
                sampleEntity.setReportId(1L);
                sampleEntity.setCustomerId(100L);
                sampleEntity.setBehaviorMetrics(metrics);
                sampleEntity.setCampaignResponse(campaign);
                sampleEntity.setCreatedAt(LocalDateTime.now());
                sampleEntity.setUpdatedAt(LocalDateTime.now());

                // Build a reusable request
                sampleRequest = new EngagementReportRequest();
                sampleRequest.setCustomerId(100L);
                sampleRequest.setAbandonedCartCount(2);
                sampleRequest.setFavouriteProduct("Wireless Mouse");
                sampleRequest.setCampaignName("Summer Sale");
                sampleRequest.setAbandonedCartReminderSent(true);
                sampleRequest.setLoyaltyPoints(100);
                sampleRequest.setResponseStatus(CampaignResponseStatus.CLICKED);
        }

        // ── Helper: build an OrderResponse ────────────────────────────────────────

        private OrderResponse orderResponse(Long orderId, Long customerId,
                        Long productId, Double totalAmount) {
                return new OrderResponse(
                                orderId, 
                                customerId, 
                                String.valueOf(productId), // <-- THE CRITICAL FIX: Convert Long to String
                                totalAmount / 2, 
                                totalAmount,
                                "DELIVERED",
                                LocalDateTime.now(),
                                LocalDateTime.now());
        }

        // ═══════════════════════════════════════════════════════════════════════════
        // getAllReports
        // ═══════════════════════════════════════════════════════════════════════════

        @Nested
        @DisplayName("getAllReports()")
        class GetAllReportsTests {

                @Test
                @DisplayName("returns all reports mapped to response DTOs")
                void returnsAllReports() {
                        when(engagementReportRepository.findAll()).thenReturn(List.of(sampleEntity));

                        List<EngagementReportResponse> result = service.getAllReports();

                        assertThat(result).hasSize(1);
                        assertThat(result.get(0).getReportId()).isEqualTo(1L);
                        assertThat(result.get(0).getCustomerId()).isEqualTo(100L);
                        verify(engagementReportRepository, times(1)).findAll();
                }

                @Test
                @DisplayName("returns empty list when no reports exist")
                void returnsEmptyWhenNone() {
                        when(engagementReportRepository.findAll()).thenReturn(Collections.emptyList());

                        List<EngagementReportResponse> result = service.getAllReports();

                        assertThat(result).isEmpty();
                }
        }

        // ═══════════════════════════════════════════════════════════════════════════
        // getReportById
        // ═══════════════════════════════════════════════════════════════════════════

        @Nested
        @DisplayName("getReportById()")
        class GetReportByIdTests {

                @Test
                @DisplayName("returns report when found")
                void returnsReportWhenFound() {
                        when(engagementReportRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));

                        EngagementReportResponse result = service.getReportById(1L);

                        assertThat(result.getReportId()).isEqualTo(1L);
                        assertThat(result.getBehaviorMetrics().getTotalOrders()).isEqualTo(5);
                        assertThat(result.getCampaignResponse().getCampaignName()).isEqualTo("Summer Sale");
                }

                @Test
                @DisplayName("throws ResourceNotFoundException when report not found")
                void throwsWhenNotFound() {
                        when(engagementReportRepository.findById(999L)).thenReturn(Optional.empty());

                        assertThatThrownBy(() -> service.getReportById(999L))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Report not found with id: 999");
                }
        }

        // ═══════════════════════════════════════════════════════════════════════════
        // getReportsByCustomerId
        // ═══════════════════════════════════════════════════════════════════════════

        @Nested
        @DisplayName("getReportsByCustomerId()")
        class GetReportsByCustomerIdTests {

                @Test
                @DisplayName("returns reports for existing customer")
                void returnsReportsForCustomer() {
                        when(engagementReportRepository.findByCustomerId(100L))
                                        .thenReturn(List.of(sampleEntity));

                        List<EngagementReportResponse> result = service.getReportsByCustomerId(100L);

                        assertThat(result).hasSize(1);
                        assertThat(result.get(0).getCustomerId()).isEqualTo(100L);
                }

                @Test
                @DisplayName("throws ResourceNotFoundException when no reports for customer")
                void throwsWhenNoReportsForCustomer() {
                        when(engagementReportRepository.findByCustomerId(999L))
                                        .thenReturn(Collections.emptyList());

                        assertThatThrownBy(() -> service.getReportsByCustomerId(999L))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("No reports found for customer id: 999");
                }
        }

        // ═══════════════════════════════════════════════════════════════════════════
        // createReport
        // ═══════════════════════════════════════════════════════════════════════════

        @Nested
        @DisplayName("createReport()")
        class CreateReportTests {

                @Test
                @DisplayName("creates report with computed behavior metrics from orders")
                void createsReportWithOrders() {
                        // Auth-service returns a valid user
                        when(authFeignClient.getUserById(100L))
                                        .thenReturn(new UserResponse(100L, "katta", "katta@test.com"));

                        // Order-service returns 3 orders (2 for product 10, 1 for product 20)
                        List<OrderResponse> orders = List.of(
                                        orderResponse(1L, 100L, 10L, 200.0),
                                        orderResponse(2L, 100L, 10L, 300.0),
                                        orderResponse(3L, 100L, 20L, 500.0));
                        when(orderFeignClient.getOrdersByCustomerId(100L)).thenReturn(orders);

                        // Catalog-service resolves product name for favourite
                        // Fix 2: Changed to expect a String to match the new CatalogFeignClient
                        // definition
                        when(catalogFeignClient.getProductById(String.valueOf(10L)))
                                        .thenReturn(new ProductResponse(10L, "Wireless Mouse", 25.0, 30.0,
                                                        "mouse.png"));

                        // Repository saves and returns
                        when(engagementReportRepository.save(any(EngagementReportEntity.class)))
                                        .thenAnswer(invocation -> {
                                                EngagementReportEntity e = invocation.getArgument(0);
                                                e.setReportId(1L);
                                                e.setCreatedAt(LocalDateTime.now());
                                                e.setUpdatedAt(LocalDateTime.now());
                                                return e;
                                        });

                        // Use a request without favouriteProduct so it's resolved from catalog
                        sampleRequest.setFavouriteProduct(null);
                        EngagementReportResponse result = service.createReport(sampleRequest);

                        assertThat(result.getReportId()).isEqualTo(1L);

                        // Verify the saved entity's behavior metrics
                        ArgumentCaptor<EngagementReportEntity> captor = ArgumentCaptor
                                        .forClass(EngagementReportEntity.class);
                        verify(engagementReportRepository).save(captor.capture());

                        BehaviorMetricsEntity savedMetrics = captor.getValue().getBehaviorMetrics();
                        assertThat(savedMetrics.getTotalOrders()).isEqualTo(3);
                        assertThat(savedMetrics.getRepeatPurchaseCount()).isEqualTo(2); // 3 - 1
                        assertThat(savedMetrics.getAbandonedCartCount()).isEqualTo(2);
                        assertThat(savedMetrics.getFavouriteProduct()).isEqualTo("Wireless Mouse");
                        // avgValue = (200 + 300 + 500) / 3 = 333.33...
                        assertThat(savedMetrics.getAverageOrderValue()).isCloseTo(333.33, within(0.01));
                }

                @Test
                @DisplayName("uses request favouriteProduct when explicitly provided")
                void usesRequestFavouriteProduct() {
                        when(authFeignClient.getUserById(100L))
                                        .thenReturn(new UserResponse(100L, "katta", "katta@test.com"));
                        when(orderFeignClient.getOrdersByCustomerId(100L))
                                        .thenReturn(List.of(orderResponse(1L, 100L, 10L, 100.0)));
                        when(engagementReportRepository.save(any()))
                                        .thenAnswer(inv -> {
                                                EngagementReportEntity e = inv.getArgument(0);
                                                e.setReportId(2L);
                                                e.setCreatedAt(LocalDateTime.now());
                                                e.setUpdatedAt(LocalDateTime.now());
                                                return e;
                                        });

                        sampleRequest.setFavouriteProduct("Keyboard Pro");
                        service.createReport(sampleRequest);

                        ArgumentCaptor<EngagementReportEntity> captor = ArgumentCaptor
                                        .forClass(EngagementReportEntity.class);
                        verify(engagementReportRepository).save(captor.capture());
                        assertThat(captor.getValue().getBehaviorMetrics().getFavouriteProduct())
                                        .isEqualTo("Keyboard Pro");

                        // catalog-service should NOT be called when favouriteProduct is already set
                        verify(catalogFeignClient, never()).getProductById(anyString()); // Fix 2b: Changed to
                                                                                         // anyString()
                }

                @Test
                @DisplayName("degrades gracefully when catalog-service is unreachable")
                void degradesWhenCatalogDown() {
                        when(authFeignClient.getUserById(100L))
                                        .thenReturn(new UserResponse(100L, "katta", "katta@test.com"));
                        when(orderFeignClient.getOrdersByCustomerId(100L))
                                        .thenReturn(List.of(orderResponse(1L, 100L, 42L, 100.0)));
                        // Fix 2c: Changed to String.valueOf(42L) to match the expected String parameter
                        when(catalogFeignClient.getProductById(String.valueOf(42L)))
                                        .thenThrow(new RuntimeException("Connection refused"));
                        when(engagementReportRepository.save(any()))
                                        .thenAnswer(inv -> {
                                                EngagementReportEntity e = inv.getArgument(0);
                                                e.setReportId(3L);
                                                e.setCreatedAt(LocalDateTime.now());
                                                e.setUpdatedAt(LocalDateTime.now());
                                                return e;
                                        });

                        sampleRequest.setFavouriteProduct(null);
                        EngagementReportResponse result = service.createReport(sampleRequest);

                        assertThat(result).isNotNull();
                        ArgumentCaptor<EngagementReportEntity> captor = ArgumentCaptor
                                        .forClass(EngagementReportEntity.class);
                        verify(engagementReportRepository).save(captor.capture());
                        // Falls back to "Product-42"
                        assertThat(captor.getValue().getBehaviorMetrics().getFavouriteProduct())
                                        .isEqualTo("Product-42");
                }

                @Test
                @DisplayName("throws ResourceNotFoundException when customer not found in auth-service")
                void throwsWhenCustomerNotFound() {
                        when(authFeignClient.getUserById(999L))
                                        .thenThrow(new RuntimeException("404 Not Found"));

                        sampleRequest.setCustomerId(999L);

                        assertThatThrownBy(() -> service.createReport(sampleRequest))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Customer not found in Auth Service: 999");

                        verify(engagementReportRepository, never()).save(any());
                }

                @Test
                @DisplayName("handles empty order list — zero totals, no favourite")
                void handlesEmptyOrders() {
                        when(authFeignClient.getUserById(100L))
                                        .thenReturn(new UserResponse(100L, "katta", "katta@test.com"));
                        when(orderFeignClient.getOrdersByCustomerId(100L))
                                        .thenReturn(Collections.emptyList());
                        when(engagementReportRepository.save(any()))
                                        .thenAnswer(inv -> {
                                                EngagementReportEntity e = inv.getArgument(0);
                                                e.setReportId(4L);
                                                e.setCreatedAt(LocalDateTime.now());
                                                e.setUpdatedAt(LocalDateTime.now());
                                                return e;
                                        });

                        // Fix 3: Removed unused variable 'result' if not asserting against it
                        service.createReport(sampleRequest);

                        ArgumentCaptor<EngagementReportEntity> captor = ArgumentCaptor
                                        .forClass(EngagementReportEntity.class);
                        verify(engagementReportRepository).save(captor.capture());

                        BehaviorMetricsEntity metrics = captor.getValue().getBehaviorMetrics();
                        assertThat(metrics.getTotalOrders()).isZero();
                        assertThat(metrics.getRepeatPurchaseCount()).isZero();
                        assertThat(metrics.getAverageOrderValue()).isZero();
                }

                @Test
                @DisplayName("sets correct campaign response fields")
                void setsCampaignResponseFields() {
                        when(authFeignClient.getUserById(100L))
                                        .thenReturn(new UserResponse(100L, "katta", "katta@test.com"));
                        when(orderFeignClient.getOrdersByCustomerId(100L))
                                        .thenReturn(Collections.emptyList());
                        when(engagementReportRepository.save(any()))
                                        .thenAnswer(inv -> {
                                                EngagementReportEntity e = inv.getArgument(0);
                                                e.setReportId(5L);
                                                e.setCreatedAt(LocalDateTime.now());
                                                e.setUpdatedAt(LocalDateTime.now());
                                                return e;
                                        });

                        service.createReport(sampleRequest);

                        ArgumentCaptor<EngagementReportEntity> captor = ArgumentCaptor
                                        .forClass(EngagementReportEntity.class);
                        verify(engagementReportRepository).save(captor.capture());

                        CampaignResponseEntity campaign = captor.getValue().getCampaignResponse();
                        assertThat(campaign.getCampaignName()).isEqualTo("Summer Sale");
                        assertThat(campaign.isAbandonedCartReminderSent()).isTrue();
                        assertThat(campaign.getLoyaltyPoints()).isEqualTo(100);
                        assertThat(campaign.getResponseStatus()).isEqualTo(CampaignResponseStatus.CLICKED);
                }
        }

        // ═══════════════════════════════════════════════════════════════════════════
        // updateReport
        // ═══════════════════════════════════════════════════════════════════════════

        @Nested
        @DisplayName("updateReport()")
        class UpdateReportTests {

                @Test
                @DisplayName("updates existing report with new metrics and campaign data")
                void updatesExistingReport() {
                        when(engagementReportRepository.findById(1L))
                                        .thenReturn(Optional.of(sampleEntity));
                        when(orderFeignClient.getOrdersByCustomerId(100L))
                                        .thenReturn(List.of(orderResponse(1L, 100L, 10L, 400.0)));
                        when(engagementReportRepository.save(any()))
                                        .thenAnswer(inv -> inv.getArgument(0));

                        sampleRequest.setCampaignName("Winter Promo");
                        EngagementReportResponse result = service.updateReport(1L, sampleRequest);

                        assertThat(result.getReportId()).isEqualTo(1L);
                        verify(engagementReportRepository).save(any(EngagementReportEntity.class));
                }

                @Test
                @DisplayName("throws ResourceNotFoundException when report to update not found")
                void throwsWhenUpdateTargetMissing() {
                        when(engagementReportRepository.findById(999L)).thenReturn(Optional.empty());

                        assertThatThrownBy(() -> service.updateReport(999L, sampleRequest))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Report not found with id: 999");
                }
        }

        // ═══════════════════════════════════════════════════════════════════════════
        // deleteReport
        // ═══════════════════════════════════════════════════════════════════════════

        @Nested
        @DisplayName("deleteReport()")
        class DeleteReportTests {

                @Test
                @DisplayName("deletes existing report successfully")
                void deletesExistingReport() {
                        when(engagementReportRepository.findById(1L))
                                        .thenReturn(Optional.of(sampleEntity));
                        doNothing().when(engagementReportRepository).delete(sampleEntity);

                        service.deleteReport(1L);

                        verify(engagementReportRepository).delete(sampleEntity);
                }

                @Test
                @DisplayName("throws ResourceNotFoundException when report to delete not found")
                void throwsWhenDeleteTargetMissing() {
                        when(engagementReportRepository.findById(999L)).thenReturn(Optional.empty());

                        assertThatThrownBy(() -> service.deleteReport(999L))
                                        .isInstanceOf(ResourceNotFoundException.class)
                                        .hasMessageContaining("Report not found with id: 999");

                        verify(engagementReportRepository, never()).delete(any());
                }
        }
}