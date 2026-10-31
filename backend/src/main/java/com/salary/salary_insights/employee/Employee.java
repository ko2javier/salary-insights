package com.salary.salary_insights.employee;

import com.salary.salary_insights.country.Country;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;

    private String email;

    @Convert(converter = JobTitleConverter.class)
    private JobTitle jobTitle;

    // Always derived from the job title; stored so that GROUP BY department reads it directly
    private String department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_code")
    private Country country;

    private long annualSalary;

    private LocalDate hireDate;

    protected Employee() {
    }

    public Employee(String fullName, String email, JobTitle jobTitle,
                    Country country, long annualSalary, LocalDate hireDate) {
        this.fullName = fullName;
        this.email = email;
        this.jobTitle = jobTitle;
        this.department = jobTitle.department().displayName();
        this.country = country;
        this.annualSalary = annualSalary;
        this.hireDate = hireDate;
    }

    public Integer id() {
        return id;
    }

    public String fullName() {
        return fullName;
    }

    public String email() {
        return email;
    }

    public JobTitle jobTitle() {
        return jobTitle;
    }

    public Department department() {
        return jobTitle.department();
    }

    public Country country() {
        return country;
    }

    public long annualSalary() {
        return annualSalary;
    }

    public LocalDate hireDate() {
        return hireDate;
    }
}
