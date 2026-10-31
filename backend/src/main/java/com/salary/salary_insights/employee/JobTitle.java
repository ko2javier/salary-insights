package com.salary.salary_insights.employee;

public enum JobTitle {
    SOFTWARE_ENGINEER(Department.ENGINEERING);

    private final Department department;

    JobTitle(Department department) {
        this.department = department;
    }

    public Department department() {
        return department;
    }
}
