package com.salary.salary_insights.insights;

import com.salary.salary_insights.country.CountryRepository;
import com.salary.salary_insights.employee.Employee;
import com.salary.salary_insights.employee.EmployeeRepository;
import com.salary.salary_insights.employee.JobTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.salary.salary_insights.employee.JobTitle.HR_MANAGER;
import static com.salary.salary_insights.employee.JobTitle.SOFTWARE_ENGINEER;
import static org.assertj.core.api.Assertions.assertThat;

// Six employees whose figures can be worked out by hand. Rates: EUR 1.1199, INR 0.010533 USD.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InsightsApiTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private EmployeeRepository employees;

    @Autowired
    private CountryRepository countries;

    @BeforeEach
    void sixEmployeesInThreeCountries() {
        save("Ana Garcia", SOFTWARE_ENGINEER, "ES", 40_000);      // 44,796 USD
        save("Bea Lopez", SOFTWARE_ENGINEER, "ES", 52_000);       // 58,234.8 USD
        save("Carlos Ruiz", HR_MANAGER, "ES", 60_000);            // 67,194 USD
        save("John Smith", SOFTWARE_ENGINEER, "US", 100_000);
        save("Mary Jones", HR_MANAGER, "US", 80_000);
        save("Ravi Kumar", SOFTWARE_ENGINEER, "IN", 3_000_000);   // 31,599 USD
    }

    @Test
    void eachCountryShowsItsFiguresInLocalCurrencyAndInUsd() {
        // Given
        String perCountry = "/api/insights/countries";

        // When
        MvcTestResult response = mvc.get().uri(perCountry).exchange();

        // Then: Spain pays 152,000 EUR to three people, 170,224.8 USD
        assertThat(response).hasStatus(HttpStatus.OK);
        assertThat(response).bodyJson().isLenientlyEqualTo("""
                [
                  { "name": "ES", "currency": "EUR", "headcount": 3,
                    "minSalary": 40000, "maxSalary": 60000, "averageSalary": 50667, "totalPayroll": 152000,
                    "minSalaryUsd": 44796, "maxSalaryUsd": 67194, "averageSalaryUsd": 56742, "totalPayrollUsd": 170225 },
                  { "name": "IN", "currency": "INR", "headcount": 1,
                    "minSalary": 3000000, "maxSalary": 3000000, "averageSalary": 3000000, "totalPayroll": 3000000,
                    "minSalaryUsd": 31599, "maxSalaryUsd": 31599, "averageSalaryUsd": 31599, "totalPayrollUsd": 31599 },
                  { "name": "US", "currency": "USD", "headcount": 2,
                    "minSalary": 80000, "maxSalary": 100000, "averageSalary": 90000, "totalPayroll": 180000,
                    "minSalaryUsd": 80000, "maxSalaryUsd": 100000, "averageSalaryUsd": 90000, "totalPayrollUsd": 180000 }
                ]
                """);
    }

    @Test
    void jobTitlesWithinOneCountryShowTheirOwnFigures() {
        // Given
        String jobTitlesInSpain = "/api/insights/countries/ES/job-titles";

        // When
        MvcTestResult response = mvc.get().uri(jobTitlesInSpain).exchange();

        // Then
        assertThat(response).hasStatus(HttpStatus.OK);
        assertThat(response).bodyJson().isLenientlyEqualTo("""
                [
                  { "name": "HR Manager", "currency": "EUR", "headcount": 1,
                    "minSalary": 60000, "maxSalary": 60000, "averageSalary": 60000, "totalPayroll": 60000,
                    "minSalaryUsd": 67194, "maxSalaryUsd": 67194, "averageSalaryUsd": 67194, "totalPayrollUsd": 67194 },
                  { "name": "Software Engineer", "currency": "EUR", "headcount": 2,
                    "minSalary": 40000, "maxSalary": 52000, "averageSalary": 46000, "totalPayroll": 92000,
                    "minSalaryUsd": 44796, "maxSalaryUsd": 58235, "averageSalaryUsd": 51515, "totalPayrollUsd": 103031 }
                ]
                """);
    }

    @Test
    void departmentsMixCountriesSoTheirFiguresAreInUsd() {
        // Given
        String perDepartment = "/api/insights/departments";

        // When
        MvcTestResult response = mvc.get().uri(perDepartment).exchange();

        // Then: Engineering is 44,796 + 58,234.8 + 100,000 + 31,599 = 234,629.8 USD
        assertThat(response).hasStatus(HttpStatus.OK);
        assertThat(response).bodyJson().isLenientlyEqualTo("""
                [
                  { "name": "Engineering", "headcount": 4,
                    "minSalaryUsd": 31599, "maxSalaryUsd": 100000, "averageSalaryUsd": 58657, "totalPayrollUsd": 234630 },
                  { "name": "Human Resources", "headcount": 2,
                    "minSalaryUsd": 67194, "maxSalaryUsd": 80000, "averageSalaryUsd": 73597, "totalPayrollUsd": 147194 }
                ]
                """);
    }

    @Test
    void sameJobTitleIsComparedAcrossCountries() {
        // Given
        String softwareEngineers = "/api/insights/countries?jobTitle=Software Engineer";

        // When
        MvcTestResult response = mvc.get().uri(softwareEngineers).exchange();

        // Then
        assertThat(response).hasStatus(HttpStatus.OK);
        assertThat(response).bodyJson().isLenientlyEqualTo("""
                [
                  { "name": "ES", "headcount": 2, "averageSalary": 46000, "averageSalaryUsd": 51515 },
                  { "name": "IN", "headcount": 1, "averageSalary": 3000000, "averageSalaryUsd": 31599 },
                  { "name": "US", "headcount": 1, "averageSalary": 100000, "averageSalaryUsd": 100000 }
                ]
                """);
    }

    @Test
    void unknownJobTitleIsRejected() {
        // Given
        String astronauts = "/api/insights/countries?jobTitle=Astronaut";

        // When
        MvcTestResult response = mvc.get().uri(astronauts).exchange();

        // Then
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(response).bodyJson().extractingPath("$.errors.jobTitle").isNotNull();
    }

    @Test
    void unknownCountryIsNotFound() {
        // Given
        String france = "/api/insights/countries/FR/job-titles";

        // When
        MvcTestResult response = mvc.get().uri(france).exchange();

        // Then
        assertThat(response).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void summaryGivesTheWholeCompanyInUsd() {
        // Given
        String summary = "/api/insights/summary";

        // When
        MvcTestResult response = mvc.get().uri(summary).exchange();

        // Then: 170,224.8 (Spain) + 31,599 (India) + 180,000 (US) = 381,823.8 USD for six people
        assertThat(response).hasStatus(HttpStatus.OK);
        assertThat(response).bodyJson().isStrictlyEqualTo("""
                { "headcount": 6, "countries": 3, "averageSalaryUsd": 63637, "totalPayrollUsd": 381824 }
                """);
    }

    private void save(String fullName, JobTitle jobTitle, String countryCode, long annualSalary) {
        String email = fullName.toLowerCase().replace(' ', '.') + "@acme.com";
        employees.save(new Employee(fullName, email, jobTitle,
                countries.findById(countryCode).orElseThrow(), annualSalary, LocalDate.of(2024, 1, 15)));
    }
}
