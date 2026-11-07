import { CurrencyPipe, DecimalPipe, NgTemplateOutlet } from '@angular/common';
import { Component, signal } from '@angular/core';

import { COUNTRIES, JOB_TITLES } from '../catalog';
import { LocalStats, UsdStats } from './insights.model';

@Component({
  selector: 'app-insights',
  imports: [CurrencyPipe, DecimalPipe, NgTemplateOutlet],
  templateUrl: './insights.html',
  styleUrl: './insights.scss',
})
export class Insights {
  protected readonly countries = COUNTRIES;
  protected readonly jobTitles = JOB_TITLES;

  protected readonly byCountry = signal<LocalStats[]>([]);
  protected readonly byJobTitle = signal<LocalStats[]>([]);
  protected readonly byDepartment = signal<UsdStats[]>([]);

  protected onCompareJobTitle(event: Event) {}

  protected onCountry(event: Event) {}

  protected countryName(code: string): string {
    return code;
  }
}
