import { Injectable } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';

import { Employee, EmployeeFilters, EmployeePage, EmployeeRequest } from './employee.model';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  list(filters: EmployeeFilters = {}): Observable<EmployeePage> {
    return EMPTY;
  }

  find(id: number): Observable<Employee> {
    return EMPTY;
  }

  create(request: EmployeeRequest): Observable<Employee> {
    return EMPTY;
  }

  update(id: number, request: EmployeeRequest): Observable<Employee> {
    return EMPTY;
  }

  delete(id: number): Observable<void> {
    return EMPTY;
  }
}
