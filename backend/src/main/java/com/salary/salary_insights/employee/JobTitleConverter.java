package com.salary.salary_insights.employee;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
class JobTitleConverter implements AttributeConverter<JobTitle, String> {

    @Override
    public String convertToDatabaseColumn(JobTitle jobTitle) {
        return jobTitle.displayName();
    }

    @Override
    public JobTitle convertToEntityAttribute(String displayName) {
        return JobTitle.fromDisplayName(displayName);
    }
}
