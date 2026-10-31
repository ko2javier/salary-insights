package com.salary.salary_insights.employee;

import java.time.LocalDate;

public record EmployeeRequest(
        String fullName,
        String email,
        String jobTitle,
        String countryCode,
        Long annualSalary,
        LocalDate hireDate) {
}
