package com.salary.salary_insights.seed;

import com.salary.salary_insights.country.Country;
import com.salary.salary_insights.employee.Department;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Map.entry;

final class EmployeeGenerator {

    private static final Map<String, Locale> NAME_LOCALES = Map.of(
            "US", Locale.forLanguageTag("en-US"),
            "GB", Locale.forLanguageTag("en-GB"),
            "DE", Locale.forLanguageTag("de"),
            "ES", Locale.forLanguageTag("es"),
            "MX", Locale.forLanguageTag("es-MX"),
            "IN", Locale.forLanguageTag("en-IN"));

    // DataFaker only has Chinese names in Chinese characters; these are the most common
    // ones in pinyin, family name first as they are written in China
    private static final List<String> CHINESE_FAMILY_NAMES = List.of(
            "Wang", "Li", "Zhang", "Liu", "Chen", "Yang", "Huang", "Zhao", "Wu", "Zhou",
            "Xu", "Sun", "Ma", "Zhu", "Hu", "Guo", "He", "Lin", "Luo", "Gao");

    private static final List<String> CHINESE_GIVEN_NAMES = List.of(
            "Wei", "Fang", "Na", "Min", "Jing", "Qiang", "Lei", "Jun", "Yan", "Jie",
            "Tao", "Ming", "Chao", "Hui", "Xin", "Hao", "Yu", "Ping", "Lan", "Bo");

    // Weights from docs/decisions/0001-seed-data.md
    private static final WeightedChoice<String> COUNTRY_WEIGHTS = WeightedChoice.of(
            entry("US", 25), entry("IN", 20), entry("GB", 12), entry("DE", 12),
            entry("ES", 12), entry("MX", 10), entry("CN", 9));

    private static final WeightedChoice<Department> DEPARTMENT_WEIGHTS = WeightedChoice.of(
            entry(Department.ENGINEERING, 38), entry(Department.SALES, 22),
            entry(Department.OPERATIONS, 18), entry(Department.MARKETING, 9),
            entry(Department.FINANCE, 8), entry(Department.HUMAN_RESOURCES, 5));

    private static final Map<Department, WeightedChoice<JobTitle>> JOB_TITLE_WEIGHTS = Map.of(
            Department.ENGINEERING, WeightedChoice.of(entry(JobTitle.SOFTWARE_ENGINEER, 62),
                    entry(JobTitle.SENIOR_SOFTWARE_ENGINEER, 28), entry(JobTitle.ENGINEERING_MANAGER, 10)),
            Department.SALES, WeightedChoice.of(entry(JobTitle.ACCOUNT_EXECUTIVE, 88),
                    entry(JobTitle.SALES_MANAGER, 12)),
            Department.OPERATIONS, WeightedChoice.of(entry(JobTitle.OPERATIONS_ANALYST, 88),
                    entry(JobTitle.OPERATIONS_MANAGER, 12)),
            Department.MARKETING, WeightedChoice.of(entry(JobTitle.MARKETING_SPECIALIST, 85),
                    entry(JobTitle.MARKETING_MANAGER, 15)),
            Department.FINANCE, WeightedChoice.of(entry(JobTitle.FINANCIAL_ANALYST, 85),
                    entry(JobTitle.FINANCE_MANAGER, 15)),
            Department.HUMAN_RESOURCES, WeightedChoice.of(entry(JobTitle.HR_SPECIALIST, 85),
                    entry(JobTitle.HR_MANAGER, 15)));

    private final Random random;
    private final Map<String, Country> countriesByCode;
    private final Map<String, Faker> fakers = new HashMap<>();

    EmployeeGenerator(Random random, List<Country> countries) {
        this.random = random;
        this.countriesByCode = countries.stream()
                .collect(Collectors.toMap(Country::code, Function.identity()));
    }

    List<Employee> generate(int count) {
        List<Employee> employees = new ArrayList<>(count);
        for (int number = 1; number <= count; number++) {
            Country country = countriesByCode.get(COUNTRY_WEIGHTS.pick(random.nextDouble()));
            Department department = DEPARTMENT_WEIGHTS.pick(random.nextDouble());
            JobTitle title = JOB_TITLE_WEIGHTS.get(department).pick(random.nextDouble());
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
        if (countryCode.equals("CN")) {
            return pick(CHINESE_FAMILY_NAMES) + " " + pick(CHINESE_GIVEN_NAMES);
        }
        Faker faker = fakers.computeIfAbsent(countryCode,
                code -> new Faker(NAME_LOCALES.get(code), random));
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    private String pick(List<String> names) {
        return names.get(random.nextInt(names.size()));
    }
}
