package com.salary.salary_insights.seed;

import com.salary.salary_insights.employee.JobTitle;

import java.util.Map;

final class PayScale {

    private record Band(int min, int max) {
    }

    private static final Map<JobTitle, Band> BANDS_USD = Map.of(
            JobTitle.SOFTWARE_ENGINEER, new Band(95_000, 140_000));

    private static final Map<String, Double> PAY_FACTORS = Map.of(
            "ES", 0.40);

    private PayScale() {
    }

    static double annualUsd(JobTitle title, String countryCode, double positionInBand) {
        Band band = BANDS_USD.get(title);
        double usReferenceSalary = band.min() + positionInBand * (band.max() - band.min());
        return usReferenceSalary * PAY_FACTORS.get(countryCode);
    }
}
