import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecipientDashboardComponent } from './recipient-dashboard.component';

describe('RecipientDashboardComponent', () => {
  let component: RecipientDashboardComponent;
  let fixture: ComponentFixture<RecipientDashboardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RecipientDashboardComponent]
    });
    fixture = TestBed.createComponent(RecipientDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
