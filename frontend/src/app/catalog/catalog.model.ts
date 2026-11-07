// Mirrors Catalog in backend/.../catalog: the fixed values the API accepts.

export interface Country {
  code: string;
  name: string;
  currency: string;
}

export interface JobTitle {
  name: string;
  department: string;
}

export interface Catalog {
  countries: Country[];
  departments: string[];
  jobTitles: JobTitle[];
}
