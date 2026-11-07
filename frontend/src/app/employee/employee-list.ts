import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { CatalogService } from '../catalog/catalog.service';
import { CompanySummary } from '../insights/insights.model';
import { EmployeeFilters, EmployeePage } from './employee.model';
import { EmployeeService } from './employee.service';

const NO_FILTERS: EmployeeFilters = { sort: 'desc', page: 0 };

@Component({
  selector: 'app-employee-list',
  imports: [CurrencyPipe, DecimalPipe, RouterLink],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.scss',
})
export class EmployeeList implements OnInit {
  private readonly service = inject(EmployeeService);

  protected readonly catalog = toSignal(inject(CatalogService).catalog());
  protected readonly summary = signal<CompanySummary | undefined>(undefined);

  protected readonly page = signal<EmployeePage | undefined>(undefined);
  protected readonly confirmingId = signal<number | undefined>(undefined);
  protected readonly filters = signal<EmployeeFilters>(NO_FILTERS);

  ngOnInit() {
    this.load();
  }

  protected onSearch(event: Event, search: string) {
    event.preventDefault();
    this.applyFilter('search', search.trim());
  }

  protected onFilter(name: keyof EmployeeFilters, event: Event) {
    this.applyFilter(name, (event.target as HTMLSelectElement).value);
  }

  protected reset() {}

  protected goToPage(step: number) {
    this.filters.update((filters) => ({ ...filters, page: (filters.page ?? 0) + step }));
    this.load();
  }

  protected goTo(page: number) {}

  protected isFirstPage(): boolean {
    return (this.page()?.page ?? 0) === 0;
  }

  protected isLastPage(): boolean {
    const page = this.page();
    return !page || page.page + 1 >= page.totalPages;
  }

  protected pageNumbers(): (number | null)[] {
    return [];
  }

  protected compactUsd(amount: number | undefined): string {
    return '';
  }

  // Stays on the same page, so the HR manager sees the list they were working on.
  protected delete(id: number) {
    this.service.delete(id).subscribe(() => {
      this.confirmingId.set(undefined);
      this.load();
    });
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
