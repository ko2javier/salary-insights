package com.salary.salary_insights.employee;

import com.salary.salary_insights.api.FieldConflictException;
import com.salary.salary_insights.api.InvalidFieldException;
import com.salary.salary_insights.country.Country;
import com.salary.salary_insights.country.CountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employees;
    private final CountryRepository countries;

    EmployeeService(EmployeeRepository employees, CountryRepository countries) {
        this.employees = employees;
        this.countries = countries;
    }

    public EmployeeResponse create(EmployeeRequest request) {
        if (employees.existsByEmailIgnoreCase(request.email())) {
            throw new FieldConflictException("email", "email already in use: " + request.email());
        }
        Employee employee = new Employee(
                request.fullName(),
                request.email(),
                jobTitle(request.jobTitle()),
                country(request.countryCode()),
                request.annualSalary(),
                request.hireDate());
        return EmployeeResponse.from(employees.save(employee));
    }

    private Country country(String code) {
        return countries.findById(code)
                .orElseThrow(() -> new InvalidFieldException("countryCode", "unknown country: " + code));
    }

    private static JobTitle jobTitle(String displayName) {
        try {
            return JobTitle.fromDisplayName(displayName);
        } catch (IllegalArgumentException e) {
            throw new InvalidFieldException("jobTitle", "unknown job title: " + displayName);
        }
    }
}
