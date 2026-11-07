import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';

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

describe('Insights', () => {
  let fixture: ComponentFixture<Insights>;
  let service: {
    byCountry: ReturnType<typeof vi.fn>;
    byJobTitleIn: ReturnType<typeof vi.fn>;
    byDepartment: ReturnType<typeof vi.fn>;
  };

  function text(selector: string): string {
    return (fixture.nativeElement.querySelector(selector) as HTMLElement).textContent ?? '';
  }

  function choose(selector: string, value: string) {
    const select = fixture.nativeElement.querySelector(selector) as HTMLSelectElement;
    select.value = value;
    select.dispatchEvent(new Event('change'));
    fixture.detectChanges();
  }

  beforeEach(async () => {
    service = {
      byCountry: vi.fn().mockReturnValue(of([local('DE', 'EUR', 50000, 55995)])),
      byJobTitleIn: vi.fn().mockReturnValue(of([local('HR Manager', 'USD', 90000, 90000)])),
      byDepartment: vi.fn().mockReturnValue(of([engineering])),
    };
    TestBed.configureTestingModule({
      imports: [Insights],
      providers: [{ provide: InsightsService, useValue: service }],
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
});
