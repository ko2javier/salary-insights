package com.salary.salary_insights.country;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.assertj.core.api.Assertions.assertThat;

// Use the in-memory SQLite from application-test.properties instead of looking for H2
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CountryRepositoryTest {

    @Autowired
    private CountryRepository countries;

    @Test
    void spainIsLoadedWithItsCurrencyAndExchangeRate() {
        // Given: the reference data inserted by the V2 migration

        // When
        Country spain = countries.findById("ES").orElseThrow();

        // Then
        assertThat(spain.name()).isEqualTo("Spain");
        assertThat(spain.currency()).isEqualTo("EUR");
        assertThat(spain.usdPerUnit()).isEqualTo(1.1199);
    }
}
