package com.salary.salary_insights.employee;

import com.salary.salary_insights.country.Country;
import com.salary.salary_insights.country.CountryRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// Use the in-memory SQLite from application-test.properties instead of looking for H2
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employees;

    @Autowired
    private CountryRepository countries;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void savedEmployeeIsReadBackUnchanged() {
        // Given
        Country spain = countries.findById("ES").orElseThrow();
        Employee ana = new Employee("Ana García", "ana.garcia@acme.com", JobTitle.HR_MANAGER,
                spain, 45_000, LocalDate.of(2024, 1, 15));

        // When
        Integer id = employees.save(ana).id();
        readFromTheDatabaseNotFromMemory();
        Employee found = employees.findById(id).orElseThrow();

        // Then
        assertThat(found.fullName()).isEqualTo("Ana García");
        assertThat(found.email()).isEqualTo("ana.garcia@acme.com");
        assertThat(found.jobTitle()).isEqualTo(JobTitle.HR_MANAGER);
        assertThat(found.department()).isEqualTo(Department.HUMAN_RESOURCES);
        assertThat(found.country().code()).isEqualTo("ES");
        assertThat(found.annualSalary()).isEqualTo(45_000);
        assertThat(found.hireDate()).isEqualTo(LocalDate.of(2024, 1, 15));
    }

    @Test
    void employeeIsStoredWithReadableValues() {
        // Given
        Country spain = countries.findById("ES").orElseThrow();
        Employee ana = new Employee("Ana García", "ana.garcia@acme.com", JobTitle.HR_MANAGER,
                spain, 45_000, LocalDate.of(2024, 1, 15));

        // When
        Integer id = employees.save(ana).id();
        entityManager.flush();
        Map<String, Object> row = jdbc.queryForMap(
                "SELECT job_title, department, hire_date FROM employee WHERE id = ?", id);

        // Then
        assertThat(row.get("job_title")).isEqualTo("HR Manager");
        assertThat(row.get("department")).isEqualTo("Human Resources");
        assertThat(row.get("hire_date")).isEqualTo("2024-01-15");
    }

    private void readFromTheDatabaseNotFromMemory() {
        entityManager.flush();
        entityManager.clear();
    }
}
