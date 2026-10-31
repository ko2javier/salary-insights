package com.salary.salary_insights.seed;

import com.salary.salary_insights.country.Country;
import com.salary.salary_insights.employee.Employee;
import com.salary.salary_insights.employee.JobTitle;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

final class EmployeeGenerator {

    private static final Map<String, Locale> NAME_LOCALES = Map.of(
            "US", Locale.forLanguageTag("en-US"),
            "GB", Locale.forLanguageTag("en-GB"),
            "DE", Locale.forLanguageTag("de"),
            "ES", Locale.forLanguageTag("es"),
            "MX", Locale.forLanguageTag("es-MX"),
            "IN", Locale.forLanguageTag("en-IN"),
            "CN", Locale.forLanguageTag("zh-CN"));

    private final Random random;
    private final List<Country> countries;
    private final Map<String, Faker> fakers = new HashMap<>();

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
                    fullName(country.code()),
                    "employee" + number + "@acme.example",
                    title,
                    country,
                    PayScale.toLocalCurrency(salaryUsd, country.usdPerUnit()),
                    LocalDate.of(2020, 1, 1)));
        }
        return employees;
    }

    // Every Faker shares the same Random, so one seed drives the whole run. They are
    // created on first use, in generation order, which is the same on every run.
    private String fullName(String countryCode) {
        Faker faker = fakers.computeIfAbsent(countryCode,
                code -> new Faker(NAME_LOCALES.get(code), random));
        return faker.name().firstName() + " " + faker.name().lastName();
    }
}
