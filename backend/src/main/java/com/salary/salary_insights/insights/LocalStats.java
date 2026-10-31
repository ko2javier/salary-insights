package com.salary.salary_insights.insights;

// Figures for a group inside one country: in its currency and in USD
public record LocalStats(
        String name,
        String currency,
        long headcount,
        long minSalary,
        long maxSalary,
        long averageSalary,
        long totalPayroll,
        long minSalaryUsd,
        long maxSalaryUsd,
        long averageSalaryUsd,
        long totalPayrollUsd) {
}
