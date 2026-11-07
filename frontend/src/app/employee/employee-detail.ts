import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, input, output, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

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
})
export class EmployeeDetail {
  private readonly insights = inject(InsightsService);

  readonly employee = input.required<Employee>();
  readonly closed = output<void>();
  readonly removed = output<number>();

  protected readonly catalog = toSignal(inject(CatalogService).catalog());
  protected readonly comparison = signal<LocalStats | undefined>(undefined);
  protected readonly confirmingRemove = signal(false);
  protected readonly initials = initials;

  protected countryName(): string {
    return '';
  }

  protected comparedWithAverage(peers: LocalStats): string {
    return '';
  }

  protected positionOf(amount: number, peers: LocalStats): number {
    return 0;
  }
}
