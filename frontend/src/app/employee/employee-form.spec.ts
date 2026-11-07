import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { provideTestCatalog } from '../catalog/catalog.testing';
import { EmployeeForm } from './employee-form';
import { Employee, EmployeeRequest } from './employee.model';
import { EmployeeService } from './employee.service';

const anaRequest: EmployeeRequest = {
  fullName: 'Ana Lopez',
  email: 'ana.lopez@acme.com',
  jobTitle: 'Software Engineer',
  countryCode: 'DE',
  annualSalary: 45000,
  hireDate: '2021-03-15',
};

const ana: Employee = {
  ...anaRequest,
  id: 7,
  department: 'Engineering',
  currency: 'EUR',
  annualSalaryUsd: 50396,
};

function problem(status: number, errors: Record<string, string>) {
  return throwError(() => new HttpErrorResponse({ status, error: { status, errors } }));
}

describe('EmployeeForm', () => {
  let fixture: ComponentFixture<EmployeeForm>;
  let service: { find: ReturnType<typeof vi.fn>; create: ReturnType<typeof vi.fn>; update: ReturnType<typeof vi.fn> };
  let navigate: ReturnType<typeof vi.spyOn>;

  function element<T extends HTMLElement>(selector: string): T {
    return fixture.nativeElement.querySelector(selector) as T;
  }

  function type(name: string, value: string) {
    const field = element<HTMLInputElement | HTMLSelectElement>(`[name=${name}]`);
    field.value = value;
    field.dispatchEvent(new Event(field instanceof HTMLSelectElement ? 'change' : 'input'));
  }

  function fillWith(request: EmployeeRequest) {
    type('fullName', request.fullName);
    type('email', request.email);
    type('jobTitle', request.jobTitle);
    type('countryCode', request.countryCode);
    type('annualSalary', String(request.annualSalary));
    type('hireDate', request.hireDate);
  }

  function save() {
    element<HTMLFormElement>('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();
  }

  function errorShownFor(name: string): string {
    return element(`[data-error-for=${name}]`)?.textContent?.trim() ?? '';
  }

  async function render(id?: string) {
    service = { find: vi.fn().mockReturnValue(of(ana)), create: vi.fn(), update: vi.fn() };
    TestBed.configureTestingModule({
      imports: [EmployeeForm],
      providers: [
        provideRouter([]),
        provideTestCatalog(),
        { provide: EmployeeService, useValue: service },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap(id ? { id } : {}) } },
        },
      ],
    });
    navigate = vi.spyOn(TestBed.inject(Router), 'navigateByUrl').mockResolvedValue(true);
    fixture = TestBed.createComponent(EmployeeForm);
    fixture.detectChanges();
    await fixture.whenStable();
  }

  it('creates an employee and goes back to the list', async () => {
    // Given
    await render();
    service.create.mockReturnValue(of(ana));
    fillWith(anaRequest);

    // When
    save();

    // Then
    expect(service.create).toHaveBeenCalledWith(anaRequest);
    expect(navigate).toHaveBeenCalledWith('/');
  });

  it('does not send an incomplete form and says what is missing', async () => {
    // Given
    await render();

    // When
    save();

    // Then
    expect(service.create).not.toHaveBeenCalled();
    expect(errorShownFor('fullName')).toBe('Required');
    expect(errorShownFor('email')).toBe('Required');
  });

  it('shows each error from a 400 next to its field', async () => {
    // Given
    await render();
    service.create.mockReturnValue(
      problem(400, { countryCode: 'Unknown country: XX', annualSalary: 'must be greater than 0' }),
    );
    fillWith(anaRequest);

    // When
    save();

    // Then
    expect(errorShownFor('countryCode')).toBe('Unknown country: XX');
    expect(errorShownFor('annualSalary')).toBe('must be greater than 0');
    expect(navigate).not.toHaveBeenCalled();
  });

  it('shows an email already in use next to the email', async () => {
    // Given
    await render();
    service.create.mockReturnValue(problem(409, { email: 'email already in use: ana.lopez@acme.com' }));
    fillWith(anaRequest);

    // When
    save();

    // Then
    expect(errorShownFor('email')).toBe('email already in use: ana.lopez@acme.com');
  });

  it('loads the employee being edited and saves it on its id', async () => {
    // Given
    await render('7');
    service.update.mockReturnValue(of(ana));
    fixture.detectChanges();

    // When
    type('annualSalary', '48000');
    save();

    // Then
    expect(service.find).toHaveBeenCalledWith(7);
    expect(element<HTMLInputElement>('[name=fullName]').value).toBe('Ana Lopez');
    expect(service.update).toHaveBeenCalledWith(7, { ...anaRequest, annualSalary: 48000 });
    expect(navigate).toHaveBeenCalledWith('/');
  });

  it('asks for the salary in the currency of the chosen country', async () => {
    // Given
    await render();

    // When
    type('countryCode', 'DE');
    fixture.detectChanges();

    // Then
    expect(element('[data-currency]').textContent?.trim()).toBe('EUR');
  });
});
