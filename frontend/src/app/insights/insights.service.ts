import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { LocalStats, UsdStats } from './insights.model';

const API = '/api/insights';

@Injectable({ providedIn: 'root' })
export class InsightsService {
  private readonly http = inject(HttpClient);

  // Without a job title it answers Q1 and Q4; with one, Q5.
  byCountry(jobTitle?: string): Observable<LocalStats[]> {
    const params = jobTitle ? new HttpParams().set('jobTitle', jobTitle) : new HttpParams();
    return this.http.get<LocalStats[]>(`${API}/countries`, { params });
  }

  byJobTitleIn(countryCode: string): Observable<LocalStats[]> {
    return this.http.get<LocalStats[]>(`${API}/countries/${countryCode}/job-titles`);
  }

  byDepartment(): Observable<UsdStats[]> {
    return this.http.get<UsdStats[]>(`${API}/departments`);
  }
}
