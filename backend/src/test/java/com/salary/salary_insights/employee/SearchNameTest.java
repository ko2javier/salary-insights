package com.salary.salary_insights.employee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SearchNameTest {

    @ParameterizedTest(name = "{0} is searched as jose")
    @ValueSource(strings = {"Jose", "jose", "José", "JOSE", "JOSË", "JOSÉ"})
    void everyWayOfWritingJoseIsSearchedTheSameWay(String written) {
        // Given
        String name = written;

        // When
        String normalized = SearchName.normalize(name);

        // Then
        assertThat(normalized).isEqualTo("jose");
    }

    @Test
    void umlautsLoseTheirDots() {
        // Given
        String name = "MÜLLER Jürgen";

        // When
        String normalized = SearchName.normalize(name);

        // Then
        assertThat(normalized).isEqualTo("muller jurgen");
    }

    @Test
    void eszettIsWrittenAsDoubleS() {
        // Given
        String name = "Straße";

        // When
        String normalized = SearchName.normalize(name);

        // Then
        assertThat(normalized).isEqualTo("strasse");
    }

    @Test
    void spanishAccentsAndEnyeAreRemovedToo() {
        // Given
        String name = "Íñigo Peña";

        // When
        String normalized = SearchName.normalize(name);

        // Then
        assertThat(normalized).isEqualTo("inigo pena");
    }
}
