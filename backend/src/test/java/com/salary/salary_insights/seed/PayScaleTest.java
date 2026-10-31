package com.salary.salary_insights.seed;

import com.salary.salary_insights.employee.JobTitle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

// Bands, pay factors and adjustments from docs/decisions/0002-salary-bands.md
class PayScaleTest {

    private static final double BOTTOM_OF_THE_BAND = 0.0;
    private static final double TOP_OF_THE_BAND = 1.0;

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

    @Test
    void softwareEngineerInSpainAtTheTopOfTheBandEarns56000Usd() {
        // Given
        JobTitle title = JobTitle.SOFTWARE_ENGINEER;
        String spain = "ES";

        // When
        double salary = PayScale.annualUsd(title, spain, TOP_OF_THE_BAND);

        // Then: 140,000 USD at the top of the band x 0.40, Spain's pay factor
        assertThat(salary).isCloseTo(56_000, within(0.01));
    }

    @ParameterizedTest(name = "{0} in the US earns between {1} and {2} USD")
    @CsvSource({
            "Software Engineer,         95000, 140000",
            "Senior Software Engineer, 135000, 190000",
            "Engineering Manager,      170000, 240000",
            "Account Executive,         60000, 110000",
            "Sales Manager,            110000, 170000",
            "Operations Analyst,        55000,  85000",
            "Operations Manager,        95000, 145000",
            "Marketing Specialist,      55000,  85000",
            "Marketing Manager,        100000, 155000",
            "Financial Analyst,         65000,  95000",
            "Finance Manager,          110000, 165000",
            "HR Specialist,             55000,  80000",
            "HR Manager,                95000, 140000"
    })
    void jobTitleInTheUsEarnsWithinItsBand(String storedName, int bottom, int top) {
        // Given
        JobTitle title = JobTitle.fromDisplayName(storedName);
        String unitedStates = "US";

        // When
        double atTheBottom = PayScale.annualUsd(title, unitedStates, BOTTOM_OF_THE_BAND);
        double atTheTop = PayScale.annualUsd(title, unitedStates, TOP_OF_THE_BAND);

        // Then
        assertThat(atTheBottom).isCloseTo(bottom, within(0.01));
        assertThat(atTheTop).isCloseTo(top, within(0.01));
    }

    // Sales has no department adjustment anywhere, so only the country factor shows
    @ParameterizedTest(name = "an account executive in {0} starts at {1} USD")
    @CsvSource({
            "US, 60000",
            "GB, 42000",
            "DE, 40800",
            "ES, 24000",
            "CN, 19200",
            "MX, 16800",
            "IN, 13800"
    })
    void eachCountryPaysItsShareOfTheUsBand(String countryCode, int expectedUsd) {
        // Given
        JobTitle title = JobTitle.ACCOUNT_EXECUTIVE;

        // When
        double salary = PayScale.annualUsd(title, countryCode, BOTTOM_OF_THE_BAND);

        // Then: 60,000 USD at the bottom of the band x the country's pay factor
        assertThat(salary).isCloseTo(expectedUsd, within(0.01));
    }
}
