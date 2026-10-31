package com.salary.salary_insights.seed;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class WeightedChoiceTest {

    @ParameterizedTest(name = "a roll of {0} picks the {1} option")
    @CsvSource({
            "0.0,    first",
            "0.2499, first",
            "0.25,   second",
            "0.9999, second"
    })
    void eachOptionIsPickedForItsShareOfTheRolls(double roll, String expected) {
        // Given: the first option has 25% of the weight, the second 75%
        LinkedHashMap<String, Integer> weights = new LinkedHashMap<>();
        weights.put("first", 25);
        weights.put("second", 75);
        WeightedChoice<String> choice = new WeightedChoice<>(weights);

        // When
        String picked = choice.pick(roll);

        // Then
        assertThat(picked).isEqualTo(expected);
    }
}
