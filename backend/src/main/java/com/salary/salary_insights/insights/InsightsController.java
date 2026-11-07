package com.salary.salary_insights.insights;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/insights")
class InsightsController {

    private final InsightsService service;

    InsightsController(InsightsService service) {
        this.service = service;
    }

    // Q1 and Q4 per country; with a job title, Q5
    @GetMapping("/countries")
    List<LocalStats> byCountry(@RequestParam(required = false) String jobTitle) {
        return service.byCountry(jobTitle);
    }

    // Q2
    @GetMapping("/countries/{code}/job-titles")
    List<LocalStats> byJobTitleIn(@PathVariable String code) {
        return service.byJobTitleIn(code);
    }

    // Q3 and Q4 per department
    @GetMapping("/departments")
    List<UsdStats> byDepartment() {
        return service.byDepartment();
    }

    // Q4 for the whole company: the KPIs above the employee list
    @GetMapping("/summary")
    CompanySummary summary() {
        return service.summary();
    }
}
