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
  // Every country with every job title: the KPI and the job title totals don't change while comparing
  private readonly allCountries = signal<LocalStats[]>([]);

  protected readonly comparing = signal(false);
  protected readonly selectedCountry = signal('US');

  ngOnInit() {
    this.service.summary().subscribe((summary) => this.summary.set(summary));
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
    const countryCode = (event.target as HTMLSelectElement).value;
    this.selectedCountry.set(countryCode);
    this.loadJobTitlesIn(countryCode);
  }

  protected countryName(code: string | undefined): string {
    return this.catalog()?.countries.find((country) => country.code === code)?.name ?? code ?? '';
  }

  // The country with the most employees.
  protected largestMarket(): LocalStats | undefined {
    let largest: LocalStats | undefined;
    for (const country of this.allCountries()) {
      if (!largest || country.headcount > largest.headcount) {
        largest = country;
      }
    }
    return largest;
  }

  // The selected country's own row from the per-country figures, so its total comes from SQL too.
  protected countryTotal(): LocalStats | undefined {
    return this.allCountries().find((country) => country.name === this.selectedCountry());
  }

  protected departmentOf(jobTitle: string): string {
    return this.catalog()?.jobTitles.find((title) => title.name === jobTitle)?.department ?? '';
  }

  // Percentage of the company payroll, for the bar next to each department.
  protected shareOfPayroll(department: UsdStats): number {
    const total = this.summary()?.totalPayrollUsd;
    return total ? (department.totalPayrollUsd / total) * 100 : 0;
  }

  private loadCountries(jobTitle: string | undefined) {
    this.comparing.set(jobTitle !== undefined);
    this.service.byCountry(jobTitle).subscribe((stats) => {
      this.byCountry.set(stats);
      if (jobTitle === undefined) {
        this.allCountries.set(stats);
      }
    });
  }

  private loadJobTitlesIn(countryCode: string) {
    this.service.byJobTitleIn(countryCode).subscribe((stats) => this.byJobTitle.set(stats));
  }
}
