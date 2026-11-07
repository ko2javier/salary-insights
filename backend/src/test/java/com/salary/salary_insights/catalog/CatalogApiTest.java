package com.salary.salary_insights.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogApiTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void catalogListsTheCountriesDepartmentsAndJobTitlesTheApiAccepts() {
        // Given
        String catalog = "/api/catalog";

        // When
        MvcTestResult response = mvc.get().uri(catalog).exchange();

        // Then: countries by name, departments and job titles in the order of their enums
        assertThat(response).hasStatus(HttpStatus.OK);
        assertThat(response).bodyJson().isStrictlyEqualTo("""
                {
                  "countries": [
                    { "code": "CN", "name": "China", "currency": "CNY" },
                    { "code": "DE", "name": "Germany", "currency": "EUR" },
                    { "code": "IN", "name": "India", "currency": "INR" },
                    { "code": "MX", "name": "Mexico", "currency": "MXN" },
                    { "code": "ES", "name": "Spain", "currency": "EUR" },
                    { "code": "GB", "name": "United Kingdom", "currency": "GBP" },
                    { "code": "US", "name": "United States", "currency": "USD" }
                  ],
                  "departments": ["Engineering", "Sales", "Operations", "Marketing", "Finance", "Human Resources"],
                  "jobTitles": [
                    { "name": "Software Engineer", "department": "Engineering" },
                    { "name": "Senior Software Engineer", "department": "Engineering" },
                    { "name": "Engineering Manager", "department": "Engineering" },
                    { "name": "Account Executive", "department": "Sales" },
                    { "name": "Sales Manager", "department": "Sales" },
                    { "name": "Operations Analyst", "department": "Operations" },
                    { "name": "Operations Manager", "department": "Operations" },
                    { "name": "Marketing Specialist", "department": "Marketing" },
                    { "name": "Marketing Manager", "department": "Marketing" },
                    { "name": "Financial Analyst", "department": "Finance" },
                    { "name": "Finance Manager", "department": "Finance" },
                    { "name": "HR Specialist", "department": "Human Resources" },
                    { "name": "HR Manager", "department": "Human Resources" }
                  ]
                }
                """);
    }
}
