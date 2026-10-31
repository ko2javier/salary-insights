package com.salary.salary_insights.employee;

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
        Country country = countries.findById(request.countryCode()).orElseThrow();
        Employee employee = new Employee(
                request.fullName(),
                request.email(),
                JobTitle.fromDisplayName(request.jobTitle()),
                country,
                request.annualSalary(),
                request.hireDate());
        return EmployeeResponse.from(employees.save(employee));
    }
}
