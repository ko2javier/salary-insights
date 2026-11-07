import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { provideTestCatalog } from '../catalog/catalog.testing';
import { Insights } from './insights';
import { LocalStats, UsdStats } from './insights.model';
import { InsightsService } from './insights.service';

function local(name: string, currency: string, averageSalary: number, averageSalaryUsd: number): LocalStats {
  return {
    name,
    currency,
    headcount: 1,
    minSalary: averageSalary,
    maxSalary: averageSalary,
    averageSalary,
    totalPayroll: averageSalary,
    minSalaryUsd: averageSalaryUsd,
    maxSalaryUsd: averageSalaryUsd,
    averageSalaryUsd,
    totalPayrollUsd: averageSalaryUsd,
  };
}

const engineering: UsdStats = {
  name: 'Engineering',
  headcount: 3752,
  minSalaryUsd: 28474,
  maxSalaryUsd: 238900,
  averageSalaryUsd: 82137,
  totalPayrollUsd: 308178714,
};

const unitedStates: LocalStats = { ...local('US', 'USD', 104545, 104545), headcount: 2480 };

const summary = { headcount: 10001, countries: 7, averageSalaryUsd: 60263, totalPayrollUsd: 602688723 };

describe('Insights', () => {
  let fixture: ComponentFixture<Insights>;
  let service: {
    byCountry: ReturnType<typeof vi.fn>;
    byJobTitleIn: ReturnType<typeof vi.fn>;
    byDepartment: ReturnType<typeof vi.fn>;
    summary: ReturnType<typeof vi.fn>;
  };

  function text(selector: string): string {
    const found = fixture.nativeElement.querySelector(selector) as HTMLElement | null;
    return found?.textContent?.trim() ?? '';
  }

  function choose(selector: string, value: string) {
    const select = fixture.nativeElement.querySelector(selector) as HTMLSelectElement;
    select.value = value;
    select.dispatchEvent(new Event('change'));
    fixture.detectChanges();
  }

  beforeEach(async () => {
    service = {
      byCountry: vi.fn().mockReturnValue(of([local('DE', 'EUR', 50000, 55995), unitedStates])),
      byJobTitleIn: vi.fn().mockReturnValue(of([local('HR Manager', 'USD', 90000, 90000)])),
      byDepartment: vi.fn().mockReturnValue(of([engineering])),
      summary: vi.fn().mockReturnValue(of(summary)),
    };
    TestBed.configureTestingModule({
      imports: [Insights],
      providers: [provideTestCatalog(), { provide: InsightsService, useValue: service }],
    });
    fixture = TestBed.createComponent(Insights);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('shows each country in its own currency and in USD', () => {
    // Then
    expect(service.byCountry).toHaveBeenCalledWith(undefined);
    const countries = text('#by-country tbody');
    expect(countries).toContain('Germany');
    expect(countries).toContain('€50,000');
    expect(countries).toContain('$55,995');
  });

  it('compares one job title across countries', () => {
    // When
    choose('select[name=compareJobTitle]', 'HR Manager');

    // Then
    expect(service.byCountry).toHaveBeenLastCalledWith('HR Manager');
  });

  it('shows the job titles of the first country when it opens, and of the one chosen', () => {
    // Given
    const first = service.byJobTitleIn.mock.calls[0]?.[0];

    // When
    choose('select[name=country]', 'DE');

    // Then
    expect(first).toBe('US');
    expect(service.byJobTitleIn).toHaveBeenLastCalledWith('DE');
    expect(text('#by-job-title tbody')).toContain('HR Manager');
  });

  it('shows the departments in USD', () => {
    // Then
    const departments = text('#by-department tbody');
    expect(departments).toContain('Engineering');
    expect(departments).toContain('3,752');
    expect(departments).toContain('$82,137');
  });

  it('shows the company KPIs and the largest market', () => {
    // Then
    expect(text('[data-kpi=headcount]')).toContain('10,001');
    expect(text('[data-kpi=payroll]')).toContain('$602,688,723');
    expect(text('[data-kpi=average]')).toContain('$60,263');
    expect(text('[data-kpi=largest]')).toContain('United States');
    expect(text('[data-kpi=largest]')).toContain('2,480');
  });

  it('keeps the largest market of the whole company while one job title is compared', () => {
    // Given
    service.byCountry.mockReturnValue(of([{ ...local('DE', 'EUR', 1, 1), headcount: 9 }]));

    // When
    choose('select[name=compareJobTitle]', 'HR Manager');

    // Then
    expect(text('[data-kpi=largest]')).toContain('United States');
  });

  it('closes the country table with the company total', () => {
    // Then
    const total = text('#by-country tfoot');
    expect(total).toContain('10,001');
    expect(total).toContain('$60,263');
    expect(total).toContain('$602,688,723');
  });

  it('shows the department of each job title', () => {
    // Then
    expect(text('#by-job-title tbody .chip')).toBe('Human Resources');
  });

  it('closes the job titles with the total of their country', () => {
    // Then
    const total = text('#by-job-title tfoot');
    expect(total).toContain('United States');
    expect(total).toContain('2,480');
    expect(total).toContain('$104,545');
  });

  it('shows the share of the company payroll each department takes', () => {
    // Then: 308,178,714 of 602,688,723
    expect(text('#by-department tbody')).toContain('51.1%');
  });
});
