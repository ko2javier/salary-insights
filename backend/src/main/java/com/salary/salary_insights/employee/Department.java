package com.salary.salary_insights.employee;

public enum Department {
    ENGINEERING("Engineering"),
    SALES("Sales"),
    OPERATIONS("Operations"),
    MARKETING("Marketing"),
    FINANCE("Finance"),
    HUMAN_RESOURCES("Human Resources");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
