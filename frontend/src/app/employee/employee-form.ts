import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { COUNTRIES, JOB_TITLES } from '../catalog';

@Component({
  selector: 'app-employee-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './employee-form.html',
  styleUrl: './employee-form.scss',
})
export class EmployeeForm {
  private readonly route = inject(ActivatedRoute);

  protected readonly countries = COUNTRIES;
  protected readonly jobTitles = JOB_TITLES;
  protected readonly id = Number(this.route.snapshot.paramMap.get('id')) || undefined;

  protected readonly form = new FormGroup({
    fullName: new FormControl(''),
    email: new FormControl(''),
    jobTitle: new FormControl(''),
    countryCode: new FormControl(''),
    annualSalary: new FormControl<number | null>(null),
    hireDate: new FormControl(''),
  });

  protected onSave(event: Event) {
    event.preventDefault();
  }

  protected errorFor(field: string): string {
    return '';
  }
}
