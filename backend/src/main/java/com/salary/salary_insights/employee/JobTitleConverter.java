package com.salary.salary_insights.employee;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
class JobTitleConverter implements AttributeConverter<JobTitle, String> {

    // Null when the list is not filtered by job title
    @Override
    public String convertToDatabaseColumn(JobTitle jobTitle) {
        return jobTitle == null ? null : jobTitle.displayName();
    }

    @Override
    public JobTitle convertToEntityAttribute(String displayName) {
        return JobTitle.fromDisplayName(displayName);
    }
}
