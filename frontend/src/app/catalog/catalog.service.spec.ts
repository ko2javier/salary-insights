import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { environment } from '../../environments/environment';
import { Catalog } from './catalog.model';
import { CatalogService } from './catalog.service';
import { TEST_CATALOG } from './catalog.testing';

const API = environment.apiUrl;

describe('CatalogService', () => {
  let service: CatalogService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(CatalogService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('gets the catalog from the backend', () => {
    // Given
    let result: Catalog | undefined;

    // When
    service.catalog().subscribe((catalog) => (result = catalog));
    http.expectOne(`${API}/api/catalog`).flush(TEST_CATALOG);

    // Then
    expect(result).toEqual(TEST_CATALOG);
  });

  it('asks the backend only once however many screens need it', () => {
    // Given
    service.catalog().subscribe();
    http.expectOne(`${API}/api/catalog`).flush(TEST_CATALOG);
    let second: Catalog | undefined;

    // When
    service.catalog().subscribe((catalog) => (second = catalog));

    // Then: verify() in afterEach fails if a second request went out
    expect(second).toEqual(TEST_CATALOG);
  });
});
