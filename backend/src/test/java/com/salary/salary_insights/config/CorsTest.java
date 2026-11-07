package com.salary.salary_insights.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;

// The Angular dev server runs on another port, so the browser asks before every call that changes data.
@SpringBootTest
@AutoConfigureMockMvc
class CorsTest {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void frontendOriginMayChangeEmployees() {
        // Given
        String frontend = "http://localhost:4200";

        // When
        MvcTestResult response = mvc.options().uri("/api/employees/1")
                .header("Origin", frontend)
                .header("Access-Control-Request-Method", "PUT")
                .exchange();

        // Then
        assertThat(response).hasStatus(HttpStatus.OK);
        assertThat(response).headers().hasValue("Access-Control-Allow-Origin", frontend);
    }

    @Test
    void anyOtherOriginIsRefused() {
        // Given
        String stranger = "http://evil.example";

        // When
        MvcTestResult response = mvc.options().uri("/api/employees/1")
                .header("Origin", stranger)
                .header("Access-Control-Request-Method", "PUT")
                .exchange();

        // Then
        assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
    }
}
