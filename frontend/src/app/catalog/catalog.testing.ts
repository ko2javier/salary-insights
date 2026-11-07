// Test data only: a small catalog for the component tests, served without HTTP.
import { of } from 'rxjs';

import { Catalog } from './catalog.model';
import { CatalogService } from './catalog.service';

export const TEST_CATALOG: Catalog = {
  countries: [
    { code: 'DE', name: 'Germany', currency: 'EUR' },
    { code: 'US', name: 'United States', currency: 'USD' },
  ],
  departments: ['Engineering', 'Human Resources'],
  jobTitles: [
    { name: 'Software Engineer', department: 'Engineering' },
    { name: 'HR Manager', department: 'Human Resources' },
  ],
};

export const provideTestCatalog = () => ({
  provide: CatalogService,
  useValue: { catalog: () => of(TEST_CATALOG) },
});
