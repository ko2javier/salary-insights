import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { provideTestCatalog } from '../catalog/catalog.testing';
import { InsightsService } from '../insights/insights.service';
import { EmployeeList } from './employee-list';
import { Employee, EmployeePage } from './employee.model';
import { EmployeeService } from './employee.service';

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

const summary = { headcount: 10001, countries: 7, averageSalaryUsd: 60263, totalPayrollUsd: 602688723 };

function pageOf(content: Employee[], page = 0, totalPages = 1): EmployeePage {
  return { content, page, size: 20, totalElements: content.length, totalPages };
}

describe('EmployeeList', () => {
  let fixture: ComponentFixture<EmployeeList>;
  let list: ReturnType<typeof vi.fn>;
  let remove: ReturnType<typeof vi.fn>;

  function element<T extends HTMLElement>(selector: string): T {
    return fixture.nativeElement.querySelector(selector) as T;
  }

  function choose(selector: string, value: string) {
    const select = element<HTMLSelectElement>(selector);
    select.value = value;
    select.dispatchEvent(new Event('change'));
    fixture.detectChanges();
  }

  function lastFilters() {
    return list.mock.lastCall?.[0];
  }

  async function render(firstPage: EmployeePage) {
    list = vi.fn().mockReturnValue(of(firstPage));
    remove = vi.fn().mockReturnValue(of(undefined));
    TestBed.configureTestingModule({
      imports: [EmployeeList],
      providers: [
        provideRouter([]),
        provideTestCatalog(),
        { provide: EmployeeService, useValue: { list, delete: remove } },
        { provide: InsightsService, useValue: { summary: () => of(summary) } },
      ],
    });
    fixture = TestBed.createComponent(EmployeeList);
    fixture.detectChanges();
    await fixture.whenStable();
  }

  it('shows the first page sorted by highest salary when it opens', async () => {
    // Given / When
    await render(pageOf([ana]));

    // Then
    expect(lastFilters()).toEqual({ sort: 'desc', page: 0 });
    const cells = [...fixture.nativeElement.querySelectorAll('tbody tr td')].map(
      (td: HTMLElement) => td.textContent?.trim(),
    );
    expect(cells).toContain('Ana Lopez');
    expect(cells).toContain('Software Engineer');
  });

  it('says so when no employee matches', async () => {
    // Given / When
    await render(pageOf([]));

    // Then
    expect(element('tbody').textContent).toContain('No employees match');
  });

  it('searches by name from the first page', async () => {
    // Given
    await render(pageOf([ana], 2, 3));
    element<HTMLInputElement>('input[name=search]').value = 'ana';

    // When
    element<HTMLFormElement>('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    // Then
    expect(lastFilters()).toEqual({ search: 'ana', sort: 'desc', page: 0 });
  });

  it('filters by country, department and job title together', async () => {
    // Given
    await render(pageOf([ana]));

    // When
    choose('select[name=country]', 'DE');
    choose('select[name=department]', 'Engineering');
    choose('select[name=jobTitle]', 'Software Engineer');

    // Then
    expect(lastFilters()).toEqual({
      country: 'DE',
      department: 'Engineering',
      jobTitle: 'Software Engineer',
      sort: 'desc',
      page: 0,
    });
  });

  it('drops a filter when its option goes back to all', async () => {
    // Given
    await render(pageOf([ana]));
    choose('select[name=country]', 'DE');

    // When
    choose('select[name=country]', '');

    // Then
    expect(lastFilters()).toEqual({ sort: 'desc', page: 0 });
  });

  it('sorts by lowest salary first', async () => {
    // Given
    await render(pageOf([ana]));

    // When
    choose('select[name=sort]', 'asc');

    // Then
    expect(lastFilters()).toEqual({ sort: 'asc', page: 0 });
  });

  it('moves to the next page and back', async () => {
    // Given
    await render(pageOf([ana], 0, 3));
    list.mockReturnValue(of(pageOf([ana], 1, 3)));

    // When
    element<HTMLButtonElement>('button[name=next]').click();
    fixture.detectChanges();
    const afterNext = lastFilters();
    element<HTMLButtonElement>('button[name=previous]').click();
    fixture.detectChanges();

    // Then
    expect(afterNext).toEqual({ sort: 'desc', page: 1 });
    expect(lastFilters()).toEqual({ sort: 'desc', page: 0 });
  });

  it('cannot go back from the first page or forward from the last', async () => {
    // Given / When
    await render(pageOf([ana], 0, 1));

    // Then
    expect(element<HTMLButtonElement>('button[name=previous]').disabled).toBe(true);
    expect(element<HTMLButtonElement>('button[name=next]').disabled).toBe(true);
  });

  it('deletes an employee after confirming and reloads the same page', async () => {
    // Given
    await render(pageOf([ana], 0, 3));
    element<HTMLButtonElement>('button[name=next]').click();
    fixture.detectChanges();
    list.mockClear();

    // When
    element<HTMLButtonElement>('button[name=delete]').click();
    fixture.detectChanges();
    element<HTMLButtonElement>('button[name=confirm-delete]').click();
    fixture.detectChanges();

    // Then
    expect(remove).toHaveBeenCalledWith(7);
    expect(lastFilters()).toEqual({ sort: 'desc', page: 1 });
  });

  it('keeps the employee when the delete is not confirmed', async () => {
    // Given
    await render(pageOf([ana]));

    // When
    element<HTMLButtonElement>('button[name=delete]').click();
    fixture.detectChanges();
    element<HTMLButtonElement>('button[name=cancel-delete]').click();
    fixture.detectChanges();

    // Then
    expect(remove).not.toHaveBeenCalled();
    expect(element('button[name=delete]')).not.toBeNull();
  });

  it('shows the company KPIs above the list', async () => {
    // Given / When
    await render(pageOf([ana]));

    // Then
    expect(element('[data-kpi=headcount]').textContent).toContain('10,001');
    expect(element('[data-kpi=countries]').textContent).toContain('7');
    expect(element('[data-kpi=average]').textContent).toContain('$60,263');
    expect(element('[data-kpi=payroll]').textContent).toContain('$602.7M');
  });

  it('resets the search and every filter at once', async () => {
    // Given
    await render(pageOf([ana]));
    element<HTMLInputElement>('input[name=search]').value = 'ana';
    element<HTMLFormElement>('form').dispatchEvent(new Event('submit'));
    choose('select[name=country]', 'DE');
    choose('select[name=sort]', 'asc');

    // When
    element<HTMLButtonElement>('button[name=reset]').click();
    fixture.detectChanges();

    // Then
    expect(lastFilters()).toEqual({ sort: 'desc', page: 0 });
    expect(element<HTMLInputElement>('input[name=search]').value).toBe('');
    expect(element<HTMLSelectElement>('select[name=country]').value).toBe('');
    expect(element<HTMLSelectElement>('select[name=sort]').value).toBe('desc');
  });

  it('jumps straight to the last page', async () => {
    // Given
    await render(pageOf([ana], 0, 501));

    // When
    element<HTMLButtonElement>('button[data-page="501"]')?.click();
    fixture.detectChanges();

    // Then
    expect(lastFilters()).toEqual({ sort: 'desc', page: 500 });
  });

  it('numbers the first, last and nearby pages and leaves a gap for the rest', async () => {
    // Given / When
    await render(pageOf([ana], 9, 501));

    // Then
    const labels = [...fixture.nativeElement.querySelectorAll('.pages > *')].map((e: HTMLElement) =>
      e.textContent?.trim(),
    );
    expect(labels).toEqual(['1', '…', '9', '10', '11', '…', '501']);
  });
});
