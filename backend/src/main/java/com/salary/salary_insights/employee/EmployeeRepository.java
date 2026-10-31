package com.salary.salary_insights.employee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    boolean existsByEmailIgnoreCase(String email);
}
