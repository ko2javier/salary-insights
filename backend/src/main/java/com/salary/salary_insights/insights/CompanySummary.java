package com.salary.salary_insights.insights;

// The whole company at a glance, in USD because it mixes every country
public record CompanySummary(
        long headcount,
        long countries,
        long averageSalaryUsd,
        long totalPayrollUsd) {
}
