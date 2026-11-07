package com.salary.salary_insights.catalog;

import java.util.List;

// The fixed values the API accepts, so the frontend never keeps its own copy of them.
public record Catalog(List<CountryItem> countries, List<String> departments, List<JobTitleItem> jobTitles) {

    public record CountryItem(String code, String name, String currency) {
    }

    public record JobTitleItem(String name, String department) {
    }
}
