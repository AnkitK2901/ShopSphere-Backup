import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerInsightsComponent } from './customer-insights.component';

describe('CustomerInsightsComponent', () => {
  let component: CustomerInsightsComponent;
  let fixture: ComponentFixture<CustomerInsightsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CustomerInsightsComponent]
    });
    fixture = TestBed.createComponent(CustomerInsightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
