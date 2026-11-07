import { Injectable } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';

import { LocalStats, UsdStats } from './insights.model';

@Injectable({ providedIn: 'root' })
export class InsightsService {
  byCountry(jobTitle?: string): Observable<LocalStats[]> {
    return EMPTY;
  }

  byJobTitleIn(countryCode: string): Observable<LocalStats[]> {
    return EMPTY;
  }

  byDepartment(): Observable<UsdStats[]> {
    return EMPTY;
  }
}
