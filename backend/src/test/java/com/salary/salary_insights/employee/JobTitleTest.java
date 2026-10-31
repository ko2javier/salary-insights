package com.salary.salary_insights.employee;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class JobTitleTest {

    @Test
    void softwareEngineerBelongsToEngineering() {
        // Given
        JobTitle title = JobTitle.SOFTWARE_ENGINEER;

        // When
        Department department = title.department();

        // Then
        assertThat(department).isEqualTo(Department.ENGINEERING);
    }

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
}
