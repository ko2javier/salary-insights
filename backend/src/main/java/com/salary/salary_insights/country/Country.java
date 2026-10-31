package com.salary.salary_insights.country;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "country")
public class Country {

    @Id
    private String code;

    private String name;

    private String currency;

    private double usdPerUnit;

    protected Country() {
    }

    public String code() {
        return code;
    }

    public String name() {
        return name;
    }

    public String currency() {
        return currency;
    }

    public double usdPerUnit() {
        return usdPerUnit;
    }
}
