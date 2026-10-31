package com.salary.salary_insights.seed;

import com.salary.salary_insights.country.Country;
import com.salary.salary_insights.employee.Employee;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeGeneratorTest {

    // The reference data from the V2 migration
    private static final List<Country> COUNTRIES = List.of(
            new Country("US", "United States", "USD", 1),
            new Country("ES", "Spain", "EUR", 1.1199),
            new Country("DE", "Germany", "EUR", 1.1199),
            new Country("GB", "United Kingdom", "GBP", 1.3375),
            new Country("CN", "China", "CNY", 0.149921),
            new Country("IN", "India", "INR", 0.010533),
            new Country("MX", "Mexico", "MXN", 0.053580));

    private static final long SEED = 42;

    @Test
    void generatesTheRequestedNumberOfEmployees() {
        // Given
        EmployeeGenerator generator = new EmployeeGenerator(new Random(SEED), COUNTRIES);

        // When
        List<Employee> employees = generator.generate(20);

        // Then
        assertThat(employees).hasSize(20);
    }
}
