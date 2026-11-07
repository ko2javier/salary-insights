import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';

import { environment } from '../../environments/environment';
import { Catalog } from './catalog.model';

@Injectable({ providedIn: 'root' })
export class CatalogService {
  private readonly http = inject(HttpClient);

  // The catalog never changes while the app runs: one request, and every later subscriber
  // gets the same answer from memory (shareReplay keeps the last value).
  private readonly cached = this.http
    .get<Catalog>(`${environment.apiUrl}/catalog`)
    .pipe(shareReplay(1));

  catalog(): Observable<Catalog> {
    return this.cached;
  }
}
