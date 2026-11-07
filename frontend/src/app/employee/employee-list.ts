import { CurrencyPipe } from '@angular/common';
import { Component, signal } from '@angular/core';

import { COUNTRIES, DEPARTMENTS, JOB_TITLES } from '../catalog';
import { EmployeeFilters, EmployeePage } from './employee.model';

@Component({
  selector: 'app-employee-list',
  imports: [CurrencyPipe],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.scss',
})
export class EmployeeList {
  protected readonly countries = COUNTRIES;
  protected readonly departments = DEPARTMENTS;
  protected readonly jobTitles = JOB_TITLES;

  protected readonly page = signal<EmployeePage | undefined>(undefined);

  protected onSearch(event: Event, search: string) {
    event.preventDefault();
  }

  protected onFilter(name: keyof EmployeeFilters, event: Event) {}

  protected goToPage(step: number) {}

  protected isFirstPage(): boolean {
    return false;
  }

  protected isLastPage(): boolean {
    return false;
  }
}
