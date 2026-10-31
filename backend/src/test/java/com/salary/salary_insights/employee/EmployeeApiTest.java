package com.salary.salary_insights.employee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

// Every test rolls back, so each one starts from the same database
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeApiTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private JsonMapper json;

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

    static Stream<Arguments> invalidFields() {
        return Stream.of(
                arguments("fullName", "", "a blank name"),
                arguments("fullName", "x".repeat(201), "a name longer than 200 characters"),
                arguments("email", "not-an-email", "an invalid email"),
                arguments("jobTitle", null, "a missing job title"),
                arguments("countryCode", null, "a missing country"),
                arguments("annualSalary", 0, "a salary of zero"),
                arguments("annualSalary", -1000, "a negative salary"),
                arguments("hireDate", LocalDate.now().plusDays(1).toString(), "a hire date in the future"),
                arguments("hireDate", null, "a missing hire date"));
    }

    @ParameterizedTest(name = "{2} is rejected")
    @MethodSource("invalidFields")
    void invalidEmployeeIsRejectedNamingTheFieldThatFails(String field, Object invalidValue, String description) {
        // Given: a valid employee with one invalid field
        Map<String, Object> employee = anaGarcia();
        employee.put(field, invalidValue);

        // When
        MvcTestResult response = post(json.writeValueAsString(employee));

        // Then
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(response).bodyJson().extractingPath("$.errors." + field).isNotNull();
    }

    @Test
    void errorMessagesAreInEnglishEvenForABrowserInSpanish() {
        // Given
        Map<String, Object> employee = anaGarcia();
        employee.put("annualSalary", 0);

        // When
        MvcTestResult response = mvc.post().uri("/api/employees")
                .header("Accept-Language", "es-ES")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(employee))
                .exchange();

        // Then
        assertThat(response).bodyJson().extractingPath("$.errors.annualSalary")
                .isEqualTo("must be greater than 0");
    }

    private static Map<String, Object> anaGarcia() {
        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("fullName", "Ana García");
        employee.put("email", "ana.garcia@acme.com");
        employee.put("jobTitle", "HR Manager");
        employee.put("countryCode", "ES");
        employee.put("annualSalary", 45000);
        employee.put("hireDate", "2024-01-15");
        return employee;
    }

    private MvcTestResult post(String json) {
        return mvc.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .exchange();
    }
}
