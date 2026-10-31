package com.salary.salary_insights.insights;

// Figures for a group that mixes countries, so only in USD
public record UsdStats(
        String name,
        long headcount,
        long minSalaryUsd,
        long maxSalaryUsd,
        long averageSalaryUsd,
        long totalPayrollUsd) {
}
