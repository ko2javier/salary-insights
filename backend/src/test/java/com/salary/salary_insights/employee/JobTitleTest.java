package com.salary.salary_insights.employee;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}
