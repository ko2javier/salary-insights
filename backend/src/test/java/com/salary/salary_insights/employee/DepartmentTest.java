package com.salary.salary_insights.employee;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class DepartmentTest {

    @ParameterizedTest(name = "{0} is stored as {1}")
    @CsvSource({
            "ENGINEERING,     Engineering",
            "SALES,           Sales",
            "OPERATIONS,      Operations",
            "MARKETING,       Marketing",
            "FINANCE,         Finance",
            "HUMAN_RESOURCES, Human Resources"
    })
    void departmentIsStoredWithItsReadableName(Department department, String readableName) {
        // Given: one row of the table above

        // When
        String storedName = department.displayName();

        // Then
        assertThat(storedName).isEqualTo(readableName);
    }
}
