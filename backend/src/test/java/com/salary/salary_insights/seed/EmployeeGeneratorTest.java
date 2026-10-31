package com.salary.salary_insights.seed;

import com.salary.salary_insights.country.Country;
import com.salary.salary_insights.employee.Department;
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

    @Test
    void sameSeedGeneratesTheSameEmployees() {
        // Given
        EmployeeGenerator firstGenerator = new EmployeeGenerator(new Random(SEED), COUNTRIES);
        EmployeeGenerator secondGenerator = new EmployeeGenerator(new Random(SEED), COUNTRIES);

        // When
        List<Employee> firstRun = firstGenerator.generate(20);
        List<Employee> secondRun = secondGenerator.generate(20);

        // Then
        assertThat(firstRun).usingRecursiveComparison().isEqualTo(secondRun);
    }

    // The weights in docs/decisions/0001-seed-data.md give 11.9% managers
    @Test
    void aboutOneInEightEmployeesIsAManager() {
        // Given
        EmployeeGenerator generator = new EmployeeGenerator(new Random(SEED), COUNTRIES);

        // When
        List<Employee> employees = generator.generate(10_000);

        // Then
        long managers = employees.stream()
                .filter(employee -> employee.jobTitle().displayName().endsWith("Manager"))
                .count();
        assertThat(managers).isBetween(1_000L, 1_400L);
    }

    // Weight 25 of 100 in docs/decisions/0001-seed-data.md
    @Test
    void aboutAQuarterOfEmployeesWorkInTheUnitedStates() {
        // Given
        EmployeeGenerator generator = new EmployeeGenerator(new Random(SEED), COUNTRIES);

        // When
        List<Employee> employees = generator.generate(10_000);

        // Then
        long inTheUnitedStates = employees.stream()
                .filter(employee -> employee.country().code().equals("US"))
                .count();
        assertThat(inTheUnitedStates).isBetween(2_300L, 2_700L);
    }

    // Weight 38 of 100 in docs/decisions/0001-seed-data.md
    @Test
    void engineeringHoldsAboutThirtyEightPercentOfEmployees() {
        // Given
        EmployeeGenerator generator = new EmployeeGenerator(new Random(SEED), COUNTRIES);

        // When
        List<Employee> employees = generator.generate(10_000);

        // Then
        long inEngineering = employees.stream()
                .filter(employee -> employee.department() == Department.ENGINEERING)
                .count();
        assertThat(inEngineering).isBetween(3_600L, 4_000L);
    }
}
