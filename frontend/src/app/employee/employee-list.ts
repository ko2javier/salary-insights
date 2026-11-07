import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { CatalogService } from '../catalog/catalog.service';
import { CompanySummary } from '../insights/insights.model';
import { InsightsService } from '../insights/insights.service';
import { EmployeeDetail } from './employee-detail';
import { Employee, EmployeeFilters, EmployeePage } from './employee.model';
import { EmployeeService } from './employee.service';
import { initials } from './initials';

const NO_FILTERS: EmployeeFilters = { sort: 'desc', page: 0 };

@Component({
  selector: 'app-employee-list',
  imports: [CurrencyPipe, DatePipe, DecimalPipe, EmployeeDetail],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.scss',
})
export class EmployeeList implements OnInit {
  private readonly service = inject(EmployeeService);
  private readonly insights = inject(InsightsService);

  protected readonly catalog = toSignal(inject(CatalogService).catalog());
  protected readonly summary = signal<CompanySummary | undefined>(undefined);

  protected readonly page = signal<EmployeePage | undefined>(undefined);
  protected readonly filters = signal<EmployeeFilters>(NO_FILTERS);
  protected readonly selected = signal<Employee | undefined>(undefined);
  protected readonly initials = initials;

  ngOnInit() {
    this.insights.summary().subscribe((summary) => this.summary.set(summary));
    this.load();
  }

  protected onSearch(event: Event, search: string) {
    event.preventDefault();
    this.applyFilter('search', search.trim());
  }

  protected onFilter(name: keyof EmployeeFilters, event: Event) {
    this.applyFilter(name, (event.target as HTMLSelectElement).value);
  }

  protected reset() {
    this.filters.set(NO_FILTERS);
    this.load();
  }

  protected goToPage(step: number) {
    this.filters.update((filters) => ({ ...filters, page: (filters.page ?? 0) + step }));
    this.load();
  }

  protected goTo(page: number) {
    this.filters.update((filters) => ({ ...filters, page }));
    this.load();
  }

  protected isFirstPage(): boolean {
    return (this.page()?.page ?? 0) === 0;
  }

  protected isLastPage(): boolean {
    const page = this.page();
    return !page || page.page + 1 >= page.totalPages;
  }

  // Zero-based page numbers to show: the first, the last and the ones next to the current page.
  // null marks a gap ("…") where pages are skipped.
  protected pageNumbers(): (number | null)[] {
    const page = this.page();
    if (!page || page.totalPages === 0) {
      return [];
    }
    const last = page.totalPages - 1;
    const numbers: (number | null)[] = [];
    for (let n = 0; n <= last; n++) {
      if (n === 0 || n === last || Math.abs(n - page.page) <= 1) {
        numbers.push(n);
      } else if (numbers[numbers.length - 1] !== null) {
        numbers.push(null);
      }
    }
    return numbers;
  }

  // 602,688,723 → "$602.7M": the payroll is too long to read at a glance in full.
  protected compactUsd(amount: number | undefined): string {
    if (amount === undefined) {
      return '';
    }
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      notation: 'compact',
      maximumFractionDigits: 1,
    }).format(amount);
  }

  protected select(employee: Employee) {
    this.selected.set(employee);
  }

  // Stays on the same page, so the HR manager sees the list they were working on.
  protected removeFromPanel(id: number) {
    this.service.delete(id).subscribe(() => {
      this.selected.set(undefined);
      this.load();
    });
  }

  protected countryName(code: string): string {
    return this.catalog()?.countries.find((country) => country.code === code)?.name ?? code;
  }

  // Any new filter changes the result set, so it starts again from the first page.
  // An empty value ("All ...") removes the filter.
  private applyFilter(name: keyof EmployeeFilters, value: string) {
    const filters: EmployeeFilters = { ...this.filters(), [name]: value, page: 0 };
    if (value === '') {
      delete filters[name];
    }
    this.filters.set(filters);
    this.load();
  }

  private load() {
    this.service.list(this.filters()).subscribe((page) => this.page.set(page));
  }
}
