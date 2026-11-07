import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Employee, EmployeeFilters, EmployeePage, EmployeeRequest } from './employee.model';

const API = '/api/employees';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private readonly http = inject(HttpClient);

  list(filters: EmployeeFilters = {}): Observable<EmployeePage> {
    // Only the filters actually set go in the URL; the backend applies its own defaults to the rest.
    let params = new HttpParams();
    for (const [key, value] of Object.entries(filters)) {
      if (value !== undefined && value !== '') {
        params = params.set(key, value);
      }
    }
    return this.http.get<EmployeePage>(API, { params });
  }

  find(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${API}/${id}`);
  }

  create(request: EmployeeRequest): Observable<Employee> {
    return this.http.post<Employee>(API, request);
  }

  update(id: number, request: EmployeeRequest): Observable<Employee> {
    return this.http.put<Employee>(`${API}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/${id}`);
  }
}
