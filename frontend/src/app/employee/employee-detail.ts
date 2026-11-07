import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, effect, inject, input, output, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { map, switchMap } from 'rxjs';

import { CatalogService } from '../catalog/catalog.service';
import { LocalStats } from '../insights/insights.model';
import { InsightsService } from '../insights/insights.service';
import { Employee } from './employee.model';
import { initials } from './initials';

@Component({
  selector: 'app-employee-detail',
  imports: [CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './employee-detail.html',
  styleUrl: './employee-detail.scss',
  host: { '(document:keydown.escape)': 'closed.emit()' },
})
export class EmployeeDetail {
  private readonly insights = inject(InsightsService);

  readonly employee = input.required<Employee>();
  readonly closed = output<void>();
  readonly removed = output<number>();

  protected readonly catalog = toSignal(inject(CatalogService).catalog());
  protected readonly confirmingRemove = signal(false);
  protected readonly initials = initials;

  // The figures for the employee's job title in their country: the same row as in Insights (Q2).
  // switchMap drops the answer for the previous employee if another one is selected meanwhile.
  protected readonly comparison = toSignal(
    toObservable(this.employee).pipe(
      switchMap((employee) =>
        this.insights
          .byJobTitleIn(employee.countryCode)
          .pipe(map((rows) => rows.find((row) => row.name === employee.jobTitle))),
      ),
    ),
  );

  constructor() {
    // A different employee starts without the "Remove?" question open.
    effect(() => {
      this.employee();
      this.confirmingRemove.set(false);
    });
  }

  protected countryName(): string {
    const code = this.employee().countryCode;
    return this.catalog()?.countries.find((country) => country.code === code)?.name ?? code;
  }

  // In local currency: the USD figures are rounded one by one and would drift a little.
  protected comparedWithAverage(peers: LocalStats): string {
    const percent = Math.round(((this.employee().annualSalary - peers.averageSalary) / peers.averageSalary) * 100);
    if (percent === 0) {
      return 'At the average';
    }
    return `${Math.abs(percent)}% ${percent > 0 ? 'above' : 'below'} the average`;
  }

  // Where an amount sits between the lowest (0%) and the highest (100%) salary.
  protected positionOf(amount: number, peers: LocalStats): number {
    const range = peers.maxSalary - peers.minSalary;
    return range === 0 ? 50 : ((amount - peers.minSalary) / range) * 100;
  }
}
