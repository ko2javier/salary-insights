package com.salary.salary_insights.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank @Size(max = 200) String fullName,
        @NotBlank @Email String email,
        @NotBlank String jobTitle,
        @NotBlank String countryCode,
        @NotNull @Positive Long annualSalary,
        @NotNull @PastOrPresent LocalDate hireDate) {
}
