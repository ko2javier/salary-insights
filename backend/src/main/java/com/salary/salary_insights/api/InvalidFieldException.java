package com.salary.salary_insights.api;

/** A field that passes format validation but breaks a business rule, such as an unknown country. */
public class InvalidFieldException extends RuntimeException {

    private final String field;

    public InvalidFieldException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}
