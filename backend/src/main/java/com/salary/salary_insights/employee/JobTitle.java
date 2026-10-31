package com.salary.salary_insights.employee;

public enum JobTitle {
    SOFTWARE_ENGINEER("Software Engineer", Department.ENGINEERING),
    SENIOR_SOFTWARE_ENGINEER("Senior Software Engineer", Department.ENGINEERING),
    ENGINEERING_MANAGER("Engineering Manager", Department.ENGINEERING),
    ACCOUNT_EXECUTIVE("Account Executive", Department.SALES),
    SALES_MANAGER("Sales Manager", Department.SALES),
    OPERATIONS_ANALYST("Operations Analyst", Department.OPERATIONS),
    OPERATIONS_MANAGER("Operations Manager", Department.OPERATIONS),
    MARKETING_SPECIALIST("Marketing Specialist", Department.MARKETING),
    MARKETING_MANAGER("Marketing Manager", Department.MARKETING),
    FINANCIAL_ANALYST("Financial Analyst", Department.FINANCE),
    FINANCE_MANAGER("Finance Manager", Department.FINANCE),
    HR_SPECIALIST("HR Specialist", Department.HUMAN_RESOURCES),
    HR_MANAGER("HR Manager", Department.HUMAN_RESOURCES);

    private final String displayName;
    private final Department department;

    JobTitle(String displayName, Department department) {
        this.displayName = displayName;
        this.department = department;
    }

    public String displayName() {
        return displayName;
    }

    public Department department() {
        return department;
    }

    public static JobTitle fromDisplayName(String displayName) {
        for (JobTitle title : values()) {
            if (title.displayName.equals(displayName)) {
                return title;
            }
        }
        throw new IllegalArgumentException("Unknown job title: " + displayName);
    }
}
