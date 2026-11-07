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
        { provide: InsightsService, useValue: { summary: () => of(summary), byJobTitleIn: () => of([]) } },
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
    expect(element('tbody .name').textContent?.trim()).toBe('Ana Lopez');
    expect(element('tbody .job-title').textContent?.trim()).toBe('Software Engineer');
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

  it('stays on the same page after deleting from the panel', async () => {
    // Given
    await render(pageOf([ana], 0, 3));
    element<HTMLButtonElement>('button[name=next]').click();
    fixture.detectChanges();
    element<HTMLElement>('tbody tr').click();
    fixture.detectChanges();

    // When
    element<HTMLButtonElement>('button[name=remove]').click();
    fixture.detectChanges();
    element<HTMLButtonElement>('button[name=confirm-remove]').click();
    fixture.detectChanges();

    // Then
    expect(remove).toHaveBeenCalledWith(7);
    expect(lastFilters()).toEqual({ sort: 'desc', page: 1 });
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

  it('shows one salary column: USD on top and the local amount underneath', async () => {
    // Given / When
    await render(pageOf([ana]));

    // Then
    expect(fixture.nativeElement.querySelectorAll('thead th.amount').length).toBe(1);
    expect(element('td.salary .usd').textContent?.trim()).toBe('$50,396');
    expect(element('td.salary .local').textContent?.trim()).toBe('€45,000');
  });

  it('opens the panel of the employee whose row is clicked', async () => {
    // Given
    await render(pageOf([ana]));

    // When
    element<HTMLElement>('tbody tr')?.click();
    fixture.detectChanges();

    // Then
    expect(element('app-employee-detail')?.textContent).toContain('Ana Lopez');
    expect(element('tbody tr')?.classList).toContain('selected');
  });

  it('leaves editing and deleting to the panel, not to the row', async () => {
    // Given / When
    await render(pageOf([ana]));

    // Then
    expect(element('tbody tr button')).toBeNull();
    expect(element('tbody tr a')).toBeNull();
  });

  it('opens the panel from the keyboard too', async () => {
    // Given
    await render(pageOf([ana]));
    const row = element<HTMLElement>('tbody tr');

    // When
    row.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter' }));
    fixture.detectChanges();

    // Then
    expect(row.getAttribute('tabindex')).toBe('0');
    expect(element('app-employee-detail')?.textContent).toContain('Ana Lopez');
  });

  it('deletes from the panel, closes it and reloads the page', async () => {
    // Given
    await render(pageOf([ana]));
    element<HTMLElement>('tbody tr')?.click();
    fixture.detectChanges();
    list.mockClear();

    // When
    element<HTMLButtonElement>('button[name=remove]')?.click();
    fixture.detectChanges();
    element<HTMLButtonElement>('button[name=confirm-remove]')?.click();
    fixture.detectChanges();

    // Then
    expect(remove).toHaveBeenCalledWith(7);
    expect(list).toHaveBeenCalled();
    expect(element('app-employee-detail')).toBeNull();
  });
});
