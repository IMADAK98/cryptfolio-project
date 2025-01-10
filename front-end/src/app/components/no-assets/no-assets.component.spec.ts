import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoAssetsComponent } from './no-assets.component';

describe('NoAssetsComponent', () => {
  let component: NoAssetsComponent;
  let fixture: ComponentFixture<NoAssetsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoAssetsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(NoAssetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
