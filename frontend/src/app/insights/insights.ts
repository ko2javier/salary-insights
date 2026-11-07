import { CurrencyPipe, DecimalPipe, NgTemplateOutlet } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';

import { COUNTRIES, JOB_TITLES } from '../catalog';
import { LocalStats, UsdStats } from './insights.model';
import { InsightsService } from './insights.service';

@Component({
  selector: 'app-insights',
  imports: [CurrencyPipe, DecimalPipe, NgTemplateOutlet],
  templateUrl: './insights.html',
  styleUrl: './insights.scss',
})
export class Insights implements OnInit {
  private readonly service = inject(InsightsService);

  protected readonly countries = COUNTRIES;
  protected readonly jobTitles = JOB_TITLES;

  protected readonly byCountry = signal<LocalStats[]>([]);
  protected readonly byJobTitle = signal<LocalStats[]>([]);
  protected readonly byDepartment = signal<UsdStats[]>([]);

  ngOnInit() {
    this.loadCountries(undefined);
    this.loadJobTitlesIn(COUNTRIES[0].code);
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

  protected countryName(code: string): string {
    return COUNTRIES.find((country) => country.code === code)?.name ?? code;
  }

  private loadCountries(jobTitle: string | undefined) {
    this.service.byCountry(jobTitle).subscribe((stats) => this.byCountry.set(stats));
  }

  private loadJobTitlesIn(countryCode: string) {
    this.service.byJobTitleIn(countryCode).subscribe((stats) => this.byJobTitle.set(stats));
  }
}
