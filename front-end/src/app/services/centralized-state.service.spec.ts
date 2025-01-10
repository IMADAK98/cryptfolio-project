import { TestBed } from '@angular/core/testing';

import { CentralizedStateService } from './services/centralized-state.service';

describe('CentralizedStateService', () => {
  let service: CentralizedStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CentralizedStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
