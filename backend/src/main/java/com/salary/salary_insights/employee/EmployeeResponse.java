package com.salary.salary_insights.employee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record EmployeeResponse(
        Integer id,
        String fullName,
        String email,
        String jobTitle,
        String department,
        String countryCode,
        String currency,
        long annualSalary,
        long annualSalaryUsd,
        LocalDate hireDate) {

    static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.id(),
                employee.fullName(),
                employee.email(),
                employee.jobTitle().displayName(),
                employee.department().displayName(),
                employee.country().code(),
                employee.country().currency(),
                employee.annualSalary(),
                toUsd(employee.annualSalary(), employee.country().usdPerUnit()),
                employee.hireDate());
    }

    // In double arithmetic 45,000 x 1.1199 is 50,395.49999 and rounds down. BigDecimal.valueOf
    // recovers the exact decimal rate, so the product is exact and the rounding rule explicit.
    private static long toUsd(long amount, double usdPerUnit) {
        return BigDecimal.valueOf(usdPerUnit)
                .multiply(BigDecimal.valueOf(amount))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
}
