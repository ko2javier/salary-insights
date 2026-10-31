package com.salary.salary_insights.employee;

import com.salary.salary_insights.country.Country;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    protected Employee() {
    }

    public Employee(String fullName, String email, JobTitle jobTitle,
                    Country country, long annualSalary, LocalDate hireDate) {
    }

    public Integer id() {
        return id;
    }

    public String fullName() {
        return null;
    }

    public String email() {
        return null;
    }

    public JobTitle jobTitle() {
        return null;
    }

    public Department department() {
        return null;
    }

    public Country country() {
        return null;
    }

    public long annualSalary() {
        return 0;
    }

    public LocalDate hireDate() {
        return null;
    }
}
