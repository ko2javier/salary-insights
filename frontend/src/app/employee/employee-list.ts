import { CurrencyPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { CatalogService } from '../catalog/catalog.service';
import { EmployeeFilters, EmployeePage } from './employee.model';
import { EmployeeService } from './employee.service';

@Component({
  selector: 'app-employee-list',
  imports: [CurrencyPipe, RouterLink],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.scss',
})
export class EmployeeList implements OnInit {
  private readonly service = inject(EmployeeService);

  protected readonly catalog = toSignal(inject(CatalogService).catalog());

  protected readonly page = signal<EmployeePage | undefined>(undefined);
  protected readonly confirmingId = signal<number | undefined>(undefined);
  private filters: EmployeeFilters = { sort: 'desc', page: 0 };

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

  protected goToPage(step: number) {
    this.filters = { ...this.filters, page: (this.filters.page ?? 0) + step };
    this.load();
  }

  protected isFirstPage(): boolean {
    return (this.page()?.page ?? 0) === 0;
  }

  protected isLastPage(): boolean {
    const page = this.page();
    return !page || page.page + 1 >= page.totalPages;
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
    const filters: EmployeeFilters = { ...this.filters, [name]: value, page: 0 };
    if (value === '') {
      delete filters[name];
    }
    this.filters = filters;
    this.load();
  }

  private load() {
    this.service.list(this.filters).subscribe((page) => this.page.set(page));
  }
}
