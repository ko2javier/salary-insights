package com.salary.salary_insights.employee;

import com.salary.salary_insights.country.CountryRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

// Every test rolls back, so each one starts from the same three employees
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeListApiTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private EmployeeRepository employees;

    @Autowired
    private CountryRepository countries;

    // In USD: Ana 78,393 > John 60,000 > Ravi 31,599. In local units Ravi earns the most.
    @BeforeEach
    void threeEmployeesInThreeCurrencies() {
        save("Ana Garcia", JobTitle.SOFTWARE_ENGINEER, "ES", 70_000);
        save("John Smith", JobTitle.SOFTWARE_ENGINEER, "US", 60_000);
        save("Ravi Kumar", JobTitle.HR_MANAGER, "IN", 3_000_000);
    }

    @Test
    void employeesAcrossCountriesAreSortedBySalaryInUsd() {
        // Given
        String everyone = "/api/employees";

        // When
        MvcTestResult response = mvc.get().uri(everyone).exchange();

        // Then
        assertThat(response).hasStatus(HttpStatus.OK);
        assertThat(response).bodyJson().extractingPath("$.content[*].fullName")
                .asArray().containsExactly("Ana Garcia", "John Smith", "Ravi Kumar");
    }

    @Test
    void ascendingSortPutsTheLowestSalaryFirst() {
        // Given
        String lowestFirst = "/api/employees?sort=asc";

        // When
        MvcTestResult response = mvc.get().uri(lowestFirst).exchange();

        // Then
        assertThat(response).bodyJson().extractingPath("$.content[*].fullName")
                .asArray().containsExactly("Ravi Kumar", "John Smith", "Ana Garcia");
    }

    @Test
    void filtersCombine() {
        // Given
        String engineersInSpain = "/api/employees?country=ES&department=Engineering&jobTitle=Software Engineer";

        // When
        MvcTestResult response = mvc.get().uri(engineersInSpain).exchange();

        // Then
        assertThat(response).bodyJson().extractingPath("$.content[*].fullName")
                .asArray().containsExactly("Ana Garcia");
    }

    @Test
    void searchMatchesPartOfTheNameWhateverItsCase() {
        // Given
        String search = "/api/employees?search=SMI";

        // When
        MvcTestResult response = mvc.get().uri(search).exchange();

        // Then
        assertThat(response).bodyJson().extractingPath("$.content[*].fullName")
                .asArray().containsExactly("John Smith");
    }

    @Test
    void secondPageHoldsWhatDidNotFitOnTheFirst() {
        // Given
        String secondPageOfTwo = "/api/employees?page=1&size=2";

        // When
        MvcTestResult response = mvc.get().uri(secondPageOfTwo).exchange();

        // Then
        assertThat(response).bodyJson().isLenientlyEqualTo("""
                {
                  "content": [ { "fullName": "Ravi Kumar" } ],
                  "page": 1,
                  "size": 2,
                  "totalElements": 3,
                  "totalPages": 2
                }
                """);
    }

    @Test
    void unknownJobTitleFilterIsRejected() {
        // Given
        String astronauts = "/api/employees?jobTitle=Astronaut";

        // When
        MvcTestResult response = mvc.get().uri(astronauts).exchange();

        // Then
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(response).bodyJson().extractingPath("$.errors.jobTitle").isNotNull();
    }

    private void save(String fullName, JobTitle jobTitle, String countryCode, long annualSalary) {
        String email = fullName.toLowerCase().replace(' ', '.') + "@acme.com";
        employees.save(new Employee(fullName, email, jobTitle,
                countries.findById(countryCode).orElseThrow(), annualSalary, LocalDate.of(2024, 1, 15)));
    }
}
