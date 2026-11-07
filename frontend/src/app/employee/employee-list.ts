import { CurrencyPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';

import { COUNTRIES, DEPARTMENTS, JOB_TITLES } from '../catalog';
import { EmployeeFilters, EmployeePage } from './employee.model';
import { EmployeeService } from './employee.service';

@Component({
  selector: 'app-employee-list',
  imports: [CurrencyPipe],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.scss',
})
export class EmployeeList implements OnInit {
  private readonly service = inject(EmployeeService);

  protected readonly countries = COUNTRIES;
  protected readonly departments = DEPARTMENTS;
  protected readonly jobTitles = JOB_TITLES;

  protected readonly page = signal<EmployeePage | undefined>(undefined);
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
