import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { LocalStats, UsdStats } from './insights.model';
import { InsightsService } from './insights.service';

const germany: LocalStats = {
  name: 'DE',
  currency: 'EUR',
  headcount: 2,
  minSalary: 40000,
  maxSalary: 60000,
  averageSalary: 50000,
  totalPayroll: 100000,
  minSalaryUsd: 44796,
  maxSalaryUsd: 67194,
  averageSalaryUsd: 55995,
  totalPayrollUsd: 111990,
};

const engineering: UsdStats = {
  name: 'Engineering',
  headcount: 2,
  minSalaryUsd: 44796,
  maxSalaryUsd: 67194,
  averageSalaryUsd: 55995,
  totalPayrollUsd: 111990,
};

describe('InsightsService', () => {
  let service: InsightsService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(InsightsService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('gets the figures per country for every job title', () => {
    // Given
    let result: LocalStats[] | undefined;

    // When
    service.byCountry().subscribe((stats) => (result = stats));
    const request = http.expectOne((r) => r.url === '/api/insights/countries');
    request.flush([germany]);

    // Then
    expect(request.request.params.keys()).toEqual([]);
    expect(result).toEqual([germany]);
  });

  it('gets the figures per country for one job title', () => {
    // When
    service.byCountry('HR Manager').subscribe();
    const request = http.expectOne((r) => r.url === '/api/insights/countries');
    request.flush([germany]);

    // Then
    expect(request.request.params.get('jobTitle')).toBe('HR Manager');
  });

  it('gets the figures per job title within a country', () => {
    // Given
    let result: LocalStats[] | undefined;

    // When
    service.byJobTitleIn('DE').subscribe((stats) => (result = stats));
    http.expectOne('/api/insights/countries/DE/job-titles').flush([germany]);

    // Then
    expect(result).toEqual([germany]);
  });

  it('gets the figures per department', () => {
    // Given
    let result: UsdStats[] | undefined;

    // When
    service.byDepartment().subscribe((stats) => (result = stats));
    http.expectOne('/api/insights/departments').flush([engineering]);

    // Then
    expect(result).toEqual([engineering]);
  });
});
