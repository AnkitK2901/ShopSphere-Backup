import { TestBed } from '@angular/core/testing';

import { Artisan } from './artisan';

describe('Artisan', () => {
  let service: Artisan;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Artisan);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
