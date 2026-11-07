// Mirrors the backend records in backend/.../employee. Dates travel as ISO strings (yyyy-MM-dd).

export interface Employee {
  id: number;
  fullName: string;
  email: string;
  jobTitle: string;
  department: string;
  countryCode: string;
  currency: string;
  annualSalary: number;
  annualSalaryUsd: number;
  hireDate: string;
}

export interface EmployeeRequest {
  fullName: string;
  email: string;
  jobTitle: string;
  countryCode: string;
  annualSalary: number;
  hireDate: string;
}

export interface EmployeePage {
  content: Employee[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface EmployeeFilters {
  search?: string;
  country?: string;
  department?: string;
  jobTitle?: string;
  sort?: 'asc' | 'desc';
  page?: number;
  size?: number;
}
