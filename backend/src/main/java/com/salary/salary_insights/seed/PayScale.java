package com.salary.salary_insights.seed;

import com.salary.salary_insights.employee.JobTitle;

import java.util.Map;

final class PayScale {

    private record Band(int min, int max) {
    }

    private static final Map<JobTitle, Band> BANDS_USD = Map.ofEntries(
            Map.entry(JobTitle.SOFTWARE_ENGINEER, new Band(95_000, 140_000)),
            Map.entry(JobTitle.SENIOR_SOFTWARE_ENGINEER, new Band(135_000, 190_000)),
            Map.entry(JobTitle.ENGINEERING_MANAGER, new Band(170_000, 240_000)),
            Map.entry(JobTitle.ACCOUNT_EXECUTIVE, new Band(60_000, 110_000)),
            Map.entry(JobTitle.SALES_MANAGER, new Band(110_000, 170_000)),
            Map.entry(JobTitle.OPERATIONS_ANALYST, new Band(55_000, 85_000)),
            Map.entry(JobTitle.OPERATIONS_MANAGER, new Band(95_000, 145_000)),
            Map.entry(JobTitle.MARKETING_SPECIALIST, new Band(55_000, 85_000)),
            Map.entry(JobTitle.MARKETING_MANAGER, new Band(100_000, 155_000)),
            Map.entry(JobTitle.FINANCIAL_ANALYST, new Band(65_000, 95_000)),
            Map.entry(JobTitle.FINANCE_MANAGER, new Band(110_000, 165_000)),
            Map.entry(JobTitle.HR_SPECIALIST, new Band(55_000, 80_000)),
            Map.entry(JobTitle.HR_MANAGER, new Band(95_000, 140_000)));

    private static final Map<String, Double> PAY_FACTORS = Map.of(
            "US", 1.00,
            "ES", 0.40);

    private PayScale() {
    }

    static double annualUsd(JobTitle title, String countryCode, double positionInBand) {
        Band band = BANDS_USD.get(title);
        double usReferenceSalary = band.min() + positionInBand * (band.max() - band.min());
        return usReferenceSalary * PAY_FACTORS.get(countryCode);
    }
}
