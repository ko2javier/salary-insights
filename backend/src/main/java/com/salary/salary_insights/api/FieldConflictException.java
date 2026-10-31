package com.salary.salary_insights.api;

/** A field whose value is already taken by another record, such as an email in use. */
public class FieldConflictException extends RuntimeException {

    private final String field;

    public FieldConflictException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}
