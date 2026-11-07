// Mirrors LocalStats and UsdStats in backend/.../insights.

// A group inside one country: figures in its currency and in USD.
export interface LocalStats {
  name: string;
  currency: string;
  headcount: number;
  minSalary: number;
  maxSalary: number;
  averageSalary: number;
  totalPayroll: number;
  minSalaryUsd: number;
  maxSalaryUsd: number;
  averageSalaryUsd: number;
  totalPayrollUsd: number;
}

// A group that mixes countries: figures only in USD.
export interface UsdStats {
  name: string;
  headcount: number;
  minSalaryUsd: number;
  maxSalaryUsd: number;
  averageSalaryUsd: number;
  totalPayrollUsd: number;
}

// The whole company: the KPIs above the employee list.
export interface CompanySummary {
  headcount: number;
  countries: number;
  averageSalaryUsd: number;
  totalPayrollUsd: number;
}
