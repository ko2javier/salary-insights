package com.salary.salary_insights.employee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

// Every test rolls back, so each one starts from the same database
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeApiTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void addedEmployeeComesBackWithItsDepartmentAndSalaryInUsd() {
        // Given
        String anaGarcia = """
                {
                  "fullName": "Ana García",
                  "email": "ana.garcia@acme.com",
                  "jobTitle": "HR Manager",
                  "countryCode": "ES",
                  "annualSalary": 45000,
                  "hireDate": "2024-01-15"
                }
                """;

        // When
        MvcTestResult response = post(anaGarcia);

        // Then: 45,000 EUR x 1.1199 = 50,395.5 USD, rounded
        assertThat(response).hasStatus(HttpStatus.CREATED);
        assertThat(response).bodyJson().isLenientlyEqualTo("""
                {
                  "fullName": "Ana García",
                  "email": "ana.garcia@acme.com",
                  "jobTitle": "HR Manager",
                  "department": "Human Resources",
                  "countryCode": "ES",
                  "currency": "EUR",
                  "annualSalary": 45000,
                  "annualSalaryUsd": 50396,
                  "hireDate": "2024-01-15"
                }
                """);
        assertThat(response.getResponse().getHeader("Location")).matches("/api/employees/\\d+");
    }

    private MvcTestResult post(String json) {
        return mvc.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .exchange();
    }
}
