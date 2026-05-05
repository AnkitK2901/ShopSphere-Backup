import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ArtisanService } from './artisan';

describe('ArtisanService', () => {
  let service: ArtisanService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        // We must provide the HTTP tools so the test doesn't crash!
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ArtisanService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});