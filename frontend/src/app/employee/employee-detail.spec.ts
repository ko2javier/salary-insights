import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { provideTestCatalog } from '../catalog/catalog.testing';
import { LocalStats } from '../insights/insights.model';
import { InsightsService } from '../insights/insights.service';
import { EmployeeDetail } from './employee-detail';
import { Employee } from './employee.model';

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

// Software Engineers in Germany: Ana earns 45,000 against an average of 50,000, 10% below
const softwareEngineersInGermany: LocalStats = {
  name: 'Software Engineer',
  currency: 'EUR',
  headcount: 3,
  minSalary: 40000,
  maxSalary: 60000,
  averageSalary: 50000,
  totalPayroll: 150000,
  minSalaryUsd: 44796,
  maxSalaryUsd: 67194,
  averageSalaryUsd: 55995,
  totalPayrollUsd: 167985,
};

describe('EmployeeDetail', () => {
  let fixture: ComponentFixture<EmployeeDetail>;
  let byJobTitleIn: ReturnType<typeof vi.fn>;

  function text(selector: string): string {
    const found = fixture.nativeElement.querySelector(selector) as HTMLElement | null;
    return found?.textContent?.replace(/\s+/g, ' ').trim() ?? '';
  }

  function click(selector: string) {
    (fixture.nativeElement.querySelector(selector) as HTMLElement | null)?.click();
    fixture.detectChanges();
  }

  beforeEach(async () => {
    byJobTitleIn = vi.fn().mockReturnValue(
      of([{ ...softwareEngineersInGermany, name: 'HR Manager' }, softwareEngineersInGermany]),
    );
    TestBed.configureTestingModule({
      imports: [EmployeeDetail],
      providers: [provideRouter([]), provideTestCatalog(), { provide: InsightsService, useValue: { byJobTitleIn } }],
    });
    fixture = TestBed.createComponent(EmployeeDetail);
    fixture.componentRef.setInput('employee', ana);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('shows who the employee is and their salary in USD and in local currency', () => {
    // Then
    expect(text('.panel-header')).toContain('Ana Lopez');
    expect(text('.panel-header')).toContain('Software Engineer · Engineering');
    expect(text('.salary')).toContain('$50,396');
    expect(text('.salary')).toContain('€45,000 in local currency');
  });

  it('compares the salary with everyone in the same job title and country', () => {
    // Then
    expect(byJobTitleIn).toHaveBeenCalledWith('DE');
    const comparison = text('.comparison');
    expect(comparison).toContain('10% below the average');
    expect(comparison).toContain('Compared with every Software Engineer in Germany');
    expect(text('.comparison .lowest')).toContain('$44,796');
    expect(text('.comparison .average')).toContain('$55,995');
    expect(text('.comparison .highest')).toContain('$67,194');
  });

  it('asks before removing the employee', () => {
    // Given
    const removed = vi.fn();
    fixture.componentInstance.removed.subscribe(removed);

    // When
    click('button[name=remove]');
    const askedFirst = removed.mock.calls.length === 0 && text('.panel-footer').includes('Remove Ana Lopez?');
    click('button[name=confirm-remove]');

    // Then
    expect(askedFirst).toBe(true);
    expect(removed).toHaveBeenCalledWith(7);
  });

  it('links to the form to edit the details', () => {
    // Then
    const edit = fixture.nativeElement.querySelector('a[name=edit]') as HTMLAnchorElement | null;
    expect(edit?.getAttribute('href')).toBe('/employees/7/edit');
  });

  it('closes with the close button', () => {
    // Given
    const closed = vi.fn();
    fixture.componentInstance.closed.subscribe(closed);

    // When
    click('button[name=close]');

    // Then
    expect(closed).toHaveBeenCalled();
  });
});
