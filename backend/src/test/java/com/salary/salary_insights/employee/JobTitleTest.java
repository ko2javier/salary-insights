package com.salary.salary_insights.employee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class JobTitleTest {

    @Test
    void jobTitleIsFoundByTheNameStoredInTheDatabase() {
        // Given
        String storedName = "Software Engineer";

        // When
        JobTitle title = JobTitle.fromDisplayName(storedName);

        // Then
        assertThat(title).isEqualTo(JobTitle.SOFTWARE_ENGINEER);
    }

    @Test
    void unknownJobTitleIsRejected() {
        // Given
        String storedName = "Astronaut";

        // When
        Throwable thrown = catchThrowable(() -> JobTitle.fromDisplayName(storedName));

        // Then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown job title: Astronaut");
    }

    // The catalog from docs/decisions/0001-seed-data.md
    @ParameterizedTest(name = "{0} belongs to {1}")
    @CsvSource({
            "Software Engineer,        ENGINEERING",
            "Senior Software Engineer, ENGINEERING",
            "Engineering Manager,      ENGINEERING",
            "Account Executive,        SALES",
            "Sales Manager,            SALES",
            "Operations Analyst,       OPERATIONS",
            "Operations Manager,       OPERATIONS",
            "Marketing Specialist,     MARKETING",
            "Marketing Manager,        MARKETING",
            "Financial Analyst,        FINANCE",
            "Finance Manager,          FINANCE",
            "HR Specialist,            HUMAN_RESOURCES",
            "HR Manager,               HUMAN_RESOURCES"
    })
    void jobTitleBelongsToItsDepartment(String storedName, Department expectedDepartment) {
        // Given
        JobTitle title = JobTitle.fromDisplayName(storedName);

        // When
        Department department = title.department();

        // Then
        assertThat(department).isEqualTo(expectedDepartment);
    }
}
