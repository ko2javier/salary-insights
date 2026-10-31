package com.salary.salary_insights.seed;

import com.salary.salary_insights.country.Country;
import com.salary.salary_insights.employee.Employee;
import com.salary.salary_insights.employee.JobTitle;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class EmployeeGenerator {

    private final Random random;
    private final List<Country> countries;
    private final Faker faker = new Faker();

    EmployeeGenerator(Random random, List<Country> countries) {
        this.random = random;
        this.countries = countries;
    }

    List<Employee> generate(int count) {
        List<Employee> employees = new ArrayList<>(count);
        for (int number = 1; number <= count; number++) {
            Country country = countries.get(random.nextInt(countries.size()));
            JobTitle title = JobTitle.values()[random.nextInt(JobTitle.values().length)];
            double salaryUsd = PayScale.annualUsd(title, country.code(), random.nextDouble());
            employees.add(new Employee(
                    faker.name().firstName() + " " + faker.name().lastName(),
                    "employee" + number + "@acme.example",
                    title,
                    country,
                    PayScale.toLocalCurrency(salaryUsd, country.usdPerUnit()),
                    LocalDate.of(2020, 1, 1)));
        }
        return employees;
    }
}
