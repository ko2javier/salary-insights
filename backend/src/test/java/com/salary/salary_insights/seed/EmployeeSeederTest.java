package com.salary.salary_insights.seed;

import com.salary.salary_insights.employee.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

// Its own in-memory database, so the seeded employees don't leak into other tests
@SpringBootTest(properties = {
        "seed.employees=25",
        "spring.datasource.url=jdbc:sqlite:file:seeder-test?mode=memory&cache=shared&${sqlite.url-params}"
})
class EmployeeSeederTest {

    @Autowired
    private EmployeeRepository employees;

    @Autowired
    private EmployeeSeeder seeder;

    @Test
    void seedsTheConfiguredNumberOfEmployeesWhenTheTableIsEmpty() {
        // Given: an empty database and seed.employees=25

        // When: the application starts

        // Then
        assertThat(employees.count()).isEqualTo(25);
    }

    @Test
    void leavesTheEmployeesAloneWhenTheTableIsNotEmpty() {
        // Given: the 25 employees seeded when the application started

        // When: the seeder runs again, as it would after a restart
        seeder.run(new DefaultApplicationArguments());

        // Then
        assertThat(employees.count()).isEqualTo(25);
    }
}
