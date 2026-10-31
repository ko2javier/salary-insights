package com.salary.salary_insights.seed;

import com.salary.salary_insights.country.CountryRepository;
import com.salary.salary_insights.employee.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
class EmployeeSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(EmployeeSeeder.class);

    // Fixed, so every load produces exactly the same employees
    private static final long SEED = 42;

    private final EmployeeRepository employees;
    private final CountryRepository countries;
    private final int employeesToSeed;

    EmployeeSeeder(EmployeeRepository employees, CountryRepository countries,
                   @Value("${seed.employees}") int employeesToSeed) {
        this.employees = employees;
        this.countries = countries;
        this.employeesToSeed = employeesToSeed;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (employeesToSeed == 0 || employees.count() > 0) {
            return;
        }
        long start = System.currentTimeMillis();
        EmployeeGenerator generator = new EmployeeGenerator(new Random(SEED), countries.findAll());
        employees.saveAll(generator.generate(employeesToSeed));
        log.info("Seeded {} employees in {} ms", employeesToSeed, System.currentTimeMillis() - start);
    }
}
