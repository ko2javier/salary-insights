package com.salary.salary_insights.seed;

import com.salary.salary_insights.employee.JobTitle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

// Bands, pay factors and adjustments from docs/decisions/0002-salary-bands.md
class PayScaleTest {

    private static final double BOTTOM_OF_THE_BAND = 0.0;

    @Test
    void softwareEngineerInSpainAtTheBottomOfTheBandEarns38000Usd() {
        // Given
        JobTitle title = JobTitle.SOFTWARE_ENGINEER;
        String spain = "ES";

        // When
        double salary = PayScale.annualUsd(title, spain, BOTTOM_OF_THE_BAND);

        // Then: 95,000 USD at the bottom of the band x 0.40, Spain's pay factor
        assertThat(salary).isCloseTo(38_000, within(0.01));
    }
}
