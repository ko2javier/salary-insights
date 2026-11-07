import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { CatalogService } from '../catalog/catalog.service';
import { EmployeeRequest } from './employee.model';
import { EmployeeService } from './employee.service';

@Component({
  selector: 'app-employee-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './employee-form.html',
  styleUrl: './employee-form.scss',
})
export class EmployeeForm implements OnInit {
  private readonly service = inject(EmployeeService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly catalog = toSignal(inject(CatalogService).catalog());
  protected readonly id = Number(this.route.snapshot.paramMap.get('id')) || undefined;

  // The same rules as EmployeeRequest on the backend, checked here first so obvious mistakes
  // don't need a round trip. The backend still has the last word (unknown country, email in use).
  protected readonly form = new FormGroup({
    fullName: new FormControl('', [Validators.required, Validators.maxLength(200)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    jobTitle: new FormControl('', Validators.required),
    countryCode: new FormControl('', Validators.required),
    annualSalary: new FormControl<number | null>(null, [Validators.required, Validators.min(1)]),
    hireDate: new FormControl('', Validators.required),
  });

  // Messages from the backend, by field: the "errors" map of a 400 or 409 problem detail.
  private readonly serverErrors = signal<Record<string, string>>({});

  ngOnInit() {
    if (this.id) {
      this.service.find(this.id).subscribe((employee) => this.form.patchValue(employee));
    }
  }

  protected onSave(event: Event) {
    event.preventDefault();
    this.serverErrors.set({});
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request = this.form.getRawValue() as EmployeeRequest;
    const saved = this.id ? this.service.update(this.id, request) : this.service.create(request);
    saved.subscribe({
      next: () => this.router.navigateByUrl('/'),
      error: (error: HttpErrorResponse) => this.serverErrors.set(error.error?.errors ?? {}),
    });
  }

  // The salary is typed in the currency of the chosen country.
  protected currency(): string {
    const code = this.form.controls.countryCode.value;
    return this.catalog()?.countries.find((country) => country.code === code)?.currency ?? 'local currency';
  }

  protected errorFor(field: keyof typeof this.form.controls): string {
    const serverError = this.serverErrors()[field];
    if (serverError) {
      return serverError;
    }

    const control = this.form.controls[field];
    if (!control.touched || !control.errors) {
      return '';
    }
    if (control.errors['required']) {
      return 'Required';
    }
    if (control.errors['email']) {
      return 'Not a valid email';
    }
    if (control.errors['maxlength']) {
      return 'At most 200 characters';
    }
    if (control.errors['min']) {
      return 'Must be greater than 0';
    }
    return 'Invalid value';
  }
}
