import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoPortfolioComponent } from './no-portfolio.component';

describe('NoPortfolioComponent', () => {
  let component: NoPortfolioComponent;
  let fixture: ComponentFixture<NoPortfolioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoPortfolioComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(NoPortfolioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
