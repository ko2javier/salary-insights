import { Routes } from '@angular/router';

import { EmployeeForm } from './employee/employee-form';
import { EmployeeList } from './employee/employee-list';
import { Insights } from './insights/insights';

export const routes: Routes = [
  { path: '', component: EmployeeList },
  { path: 'employees/new', component: EmployeeForm },
  { path: 'employees/:id/edit', component: EmployeeForm },
  { path: 'insights', component: Insights },
];
