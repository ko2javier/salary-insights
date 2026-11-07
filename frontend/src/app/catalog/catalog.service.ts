import { Injectable } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';

import { Catalog } from './catalog.model';

@Injectable({ providedIn: 'root' })
export class CatalogService {
  catalog(): Observable<Catalog> {
    return EMPTY;
  }
}
