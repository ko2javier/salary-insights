package com.salary.salary_insights.employee;

public enum JobTitle {
    SOFTWARE_ENGINEER("Software Engineer", Department.ENGINEERING);

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
