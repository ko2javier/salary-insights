package com.salary.salary_insights.insights;

import com.salary.salary_insights.api.InvalidFieldException;
import com.salary.salary_insights.api.NotFoundException;
import com.salary.salary_insights.country.CountryRepository;
import com.salary.salary_insights.employee.JobTitle;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Every figure is computed by SQLite, and each salary is converted to USD before it is aggregated.
// GROUP BY repeats the expression instead of the alias: "name" would resolve to the country.name column.
@Service
@Transactional(readOnly = true)
public class InsightsService {

    private static final String LOCAL_AND_USD = """
            SELECT %1$s AS name, c.currency,
                   COUNT(*) AS headcount,
                   MIN(e.annual_salary) AS min_salary,
                   MAX(e.annual_salary) AS max_salary,
                   ROUND(AVG(e.annual_salary)) AS average_salary,
                   SUM(e.annual_salary) AS total_payroll,
                   ROUND(MIN(e.annual_salary * c.usd_per_unit)) AS min_salary_usd,
                   ROUND(MAX(e.annual_salary * c.usd_per_unit)) AS max_salary_usd,
                   ROUND(AVG(e.annual_salary * c.usd_per_unit)) AS average_salary_usd,
                   ROUND(SUM(e.annual_salary * c.usd_per_unit)) AS total_payroll_usd
            FROM employee e JOIN country c ON c.code = e.country_code
            WHERE %2$s
            GROUP BY %1$s, c.currency
            ORDER BY %1$s
            """;

    private final JdbcClient jdbc;
    private final CountryRepository countries;

    InsightsService(JdbcClient jdbc, CountryRepository countries) {
        this.jdbc = jdbc;
        this.countries = countries;
    }

    public List<LocalStats> byCountry(String jobTitle) {
        String displayName = jobTitle == null || jobTitle.isBlank() ? null : jobTitle(jobTitle).displayName();
        return jdbc.sql(LOCAL_AND_USD.formatted("e.country_code", ":jobTitle IS NULL OR e.job_title = :jobTitle"))
                .param("jobTitle", displayName)
                .query(LocalStats.class)
                .list();
    }

    public List<LocalStats> byJobTitleIn(String countryCode) {
        if (!countries.existsById(countryCode)) {
            throw new NotFoundException("no country with code " + countryCode);
        }
        return jdbc.sql(LOCAL_AND_USD.formatted("e.job_title", "e.country_code = :country"))
                .param("country", countryCode)
                .query(LocalStats.class)
                .list();
    }

    public List<UsdStats> byDepartment() {
        return jdbc.sql("""
                        SELECT e.department AS name,
                               COUNT(*) AS headcount,
                               ROUND(MIN(e.annual_salary * c.usd_per_unit)) AS min_salary_usd,
                               ROUND(MAX(e.annual_salary * c.usd_per_unit)) AS max_salary_usd,
                               ROUND(AVG(e.annual_salary * c.usd_per_unit)) AS average_salary_usd,
                               ROUND(SUM(e.annual_salary * c.usd_per_unit)) AS total_payroll_usd
                        FROM employee e JOIN country c ON c.code = e.country_code
                        GROUP BY e.department
                        ORDER BY name
                        """)
                .query(UsdStats.class)
                .list();
    }

    private static JobTitle jobTitle(String displayName) {
        try {
            return JobTitle.fromDisplayName(displayName);
        } catch (IllegalArgumentException e) {
            throw new InvalidFieldException("jobTitle", "unknown job title: " + displayName);
        }
    }
}
