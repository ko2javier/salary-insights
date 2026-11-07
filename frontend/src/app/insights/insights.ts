import { CurrencyPipe, DecimalPipe, NgTemplateOutlet } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { CatalogService } from '../catalog/catalog.service';
import { CompanySummary, LocalStats, UsdStats } from './insights.model';
import { InsightsService } from './insights.service';

@Component({
  selector: 'app-insights',
  imports: [CurrencyPipe, DecimalPipe, NgTemplateOutlet],
  templateUrl: './insights.html',
  styleUrl: './insights.scss',
})
export class Insights implements OnInit {
  private readonly service = inject(InsightsService);

  protected readonly catalog = toSignal(inject(CatalogService).catalog());
  protected readonly summary = signal<CompanySummary | undefined>(undefined);

  protected readonly byCountry = signal<LocalStats[]>([]);
  protected readonly byJobTitle = signal<LocalStats[]>([]);
  protected readonly byDepartment = signal<UsdStats[]>([]);

  protected readonly comparing = signal(false);
  protected readonly selectedCountry = signal('US');

  ngOnInit() {
    this.loadCountries(undefined);
    this.loadJobTitlesIn(this.selectedCountry());
    this.service.byDepartment().subscribe((stats) => this.byDepartment.set(stats));
  }

  // "All job titles" has an empty value, which means no filter.
  protected onCompareJobTitle(event: Event) {
    const jobTitle = (event.target as HTMLSelectElement).value;
    this.loadCountries(jobTitle || undefined);
  }

  protected onCountry(event: Event) {
    this.loadJobTitlesIn((event.target as HTMLSelectElement).value);
  }

  protected countryName(code: string | undefined): string {
    return this.catalog()?.countries.find((country) => country.code === code)?.name ?? code ?? '';
  }

  protected largestMarket(): LocalStats | undefined {
    return undefined;
  }

  protected countryTotal(): LocalStats | undefined {
    return undefined;
  }

  protected departmentOf(jobTitle: string): string {
    return '';
  }

  protected shareOfPayroll(department: UsdStats): number {
    return 0;
  }

  private loadCountries(jobTitle: string | undefined) {
    this.service.byCountry(jobTitle).subscribe((stats) => this.byCountry.set(stats));
  }

  private loadJobTitlesIn(countryCode: string) {
    this.service.byJobTitleIn(countryCode).subscribe((stats) => this.byJobTitle.set(stats));
  }
}
