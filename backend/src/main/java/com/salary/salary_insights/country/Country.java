package com.salary.salary_insights.country;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "country")
public class Country {

    @Id
    private String code;

    protected Country() {
    }

    public String name() {
        return null;
    }

    public String currency() {
        return null;
    }

    public double usdPerUnit() {
        return 0;
    }
}
