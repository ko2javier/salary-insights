package com.salary.salary_insights.employee;

import com.salary.salary_insights.api.FieldConflictException;
import com.salary.salary_insights.api.InvalidFieldException;
import com.salary.salary_insights.api.NotFoundException;
import com.salary.salary_insights.country.Country;
import com.salary.salary_insights.country.CountryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
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

    @Transactional(readOnly = true)
    public EmployeeResponse find(int id) {
        return EmployeeResponse.from(existing(id));
    }

    // Sorting by the USD amount also gives the right order within one country, where the rate is the same for all
    @Transactional(readOnly = true)
    public EmployeePage list(String search, String country, String department, String jobTitle,
                             String sort, int page, int size) {
        Sort bySalaryInUsd = JpaSort.unsafe(Sort.Direction.fromString(sort), "e.annualSalary * c.usdPerUnit")
                .and(Sort.by("id"));
        Page<Employee> result = employees.search(
                blankToNull(search), blankToNull(country), blankToNull(department),
                jobTitle == null || jobTitle.isBlank() ? null : jobTitle(jobTitle),
                PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, 100), bySalaryInUsd));
        return new EmployeePage(
                result.map(EmployeeResponse::from).getContent(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    public EmployeeResponse update(int id, EmployeeRequest request) {
        Employee employee = existing(id);
        if (employees.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new FieldConflictException("email", "email already in use: " + request.email());
        }
        employee.update(
                request.fullName(),
                request.email(),
                jobTitle(request.jobTitle()),
                country(request.countryCode()),
                request.annualSalary(),
                request.hireDate());
        return EmployeeResponse.from(employee);
    }

    public void delete(int id) {
        employees.delete(existing(id));
    }

    private Employee existing(int id) {
        return employees.findById(id)
                .orElseThrow(() -> new NotFoundException("no employee with id " + id));
    }

    private Country country(String code) {
        return countries.findById(code)
                .orElseThrow(() -> new InvalidFieldException("countryCode", "unknown country: " + code));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    private static JobTitle jobTitle(String displayName) {
        try {
            return JobTitle.fromDisplayName(displayName);
        } catch (IllegalArgumentException e) {
            throw new InvalidFieldException("jobTitle", "unknown job title: " + displayName);
        }
    }
}
