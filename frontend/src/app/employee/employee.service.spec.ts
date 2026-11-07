import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { Employee, EmployeePage, EmployeeRequest } from './employee.model';
import { EmployeeService } from './employee.service';
import { environment } from '../../environments/environment';

const API = environment.apiUrl;

const ana: Employee = {
  id: 7,
  fullName: 'Ana Lopez',
  email: 'ana.lopez@acme.com',
  jobTitle: 'Software Engineer',
  department: 'Engineering',
  countryCode: 'DE',
  currency: 'EUR',
  annualSalary: 45000,
  annualSalaryUsd: 50396,
  hireDate: '2021-03-15',
};

const anaRequest: EmployeeRequest = {
  fullName: 'Ana Lopez',
  email: 'ana.lopez@acme.com',
  jobTitle: 'Software Engineer',
  countryCode: 'DE',
  annualSalary: 45000,
  hireDate: '2021-03-15',
};

describe('EmployeeService', () => {
  let service: EmployeeService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(EmployeeService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lists employees sending only the filters given', () => {
    // Given
    const page: EmployeePage = { content: [ana], page: 2, size: 20, totalElements: 41, totalPages: 3 };
    let result: EmployeePage | undefined;

    // When
    service.list({ search: 'ana', country: 'DE', page: 2 }).subscribe((p) => (result = p));
    const request = http.expectOne((r) => r.url === `${API}/api/employees`);
    request.flush(page);

    // Then
    expect(request.request.method).toBe('GET');
    expect(request.request.params.keys().sort()).toEqual(['country', 'page', 'search']);
    expect(request.request.params.get('search')).toBe('ana');
    expect(request.request.params.get('country')).toBe('DE');
    expect(request.request.params.get('page')).toBe('2');
    expect(result).toEqual(page);
  });

  it('lists employees with no parameters when no filters are given', () => {
    // When
    service.list().subscribe();
    const request = http.expectOne((r) => r.url === `${API}/api/employees`);
    request.flush({ content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 });

    // Then
    expect(request.request.params.keys()).toEqual([]);
  });

  it('finds one employee by id', () => {
    // Given
    let result: Employee | undefined;

    // When
    service.find(7).subscribe((e) => (result = e));
    const request = http.expectOne(`${API}/api/employees/7`);
    request.flush(ana);

    // Then
    expect(request.request.method).toBe('GET');
    expect(result).toEqual(ana);
  });

  it('creates an employee posting the request', () => {
    // Given
    let result: Employee | undefined;

    // When
    service.create(anaRequest).subscribe((e) => (result = e));
    const request = http.expectOne(`${API}/api/employees`);
    request.flush(ana, { status: 201, statusText: 'Created' });

    // Then
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(anaRequest);
    expect(result).toEqual(ana);
  });

  it('updates an employee putting the request on its id', () => {
    // Given
    let result: Employee | undefined;

    // When
    service.update(7, anaRequest).subscribe((e) => (result = e));
    const request = http.expectOne(`${API}/api/employees/7`);
    request.flush(ana);

    // Then
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(anaRequest);
    expect(result).toEqual(ana);
  });

  it('deletes an employee by id', () => {
    // Given
    let completed = false;

    // When
    service.delete(7).subscribe({ complete: () => (completed = true) });
    const request = http.expectOne(`${API}/api/employees/7`);
    request.flush(null, { status: 204, statusText: 'No Content' });

    // Then
    expect(request.request.method).toBe('DELETE');
    expect(completed).toBe(true);
  });
});
