import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HelperDashboardComponent } from './helper-dashboard.component';

describe('HelperDashboardComponent', () => {
  let component: HelperDashboardComponent;
  let fixture: ComponentFixture<HelperDashboardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HelperDashboardComponent]
    });
    fixture = TestBed.createComponent(HelperDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
