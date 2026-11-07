package com.salary.salary_insights.catalog;

import com.salary.salary_insights.catalog.Catalog.CountryItem;
import com.salary.salary_insights.catalog.Catalog.JobTitleItem;
import com.salary.salary_insights.country.CountryRepository;
import com.salary.salary_insights.employee.Department;
import com.salary.salary_insights.employee.JobTitle;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

// Countries come from the database; departments and job titles from their enums.
@RestController
@RequestMapping("/api/catalog")
class CatalogController {

    private final CountryRepository countries;

    CatalogController(CountryRepository countries) {
        this.countries = countries;
    }

    @GetMapping
    Catalog catalog() {
        List<CountryItem> countryItems = countries.findAll(Sort.by("name")).stream()
                .map(country -> new CountryItem(country.code(), country.name(), country.currency()))
                .toList();
        List<String> departments = Arrays.stream(Department.values())
                .map(Department::displayName)
                .toList();
        List<JobTitleItem> jobTitles = Arrays.stream(JobTitle.values())
                .map(title -> new JobTitleItem(title.displayName(), title.department().displayName()))
                .toList();
        return new Catalog(countryItems, departments, jobTitles);
    }
}
