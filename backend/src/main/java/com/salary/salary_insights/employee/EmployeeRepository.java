package com.salary.salary_insights.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);

    // A null filter matches everyone. The count query is explicit because a fetch join can't be counted.
    @Query(value = """
            select e from Employee e join fetch e.country c
            where (:search is null or lower(e.fullName) like lower(concat('%', :search, '%')))
              and (:country is null or c.code = :country)
              and (:department is null or e.department = :department)
              and (:jobTitle is null or e.jobTitle = :jobTitle)
            """,
            countQuery = """
            select count(e) from Employee e join e.country c
            where (:search is null or lower(e.fullName) like lower(concat('%', :search, '%')))
              and (:country is null or c.code = :country)
              and (:department is null or e.department = :department)
              and (:jobTitle is null or e.jobTitle = :jobTitle)
            """)
    Page<Employee> search(String search, String country, String department, JobTitle jobTitle, Pageable pageable);
}
