-- Column types are the ones Hibernate expects, so that ddl-auto=validate passes.
-- Exception: the primary key must be exactly INTEGER PRIMARY KEY, the only form
-- SQLite auto-increments; the entity maps it as Integer, not Long.
-- SQLite doesn't enforce text lengths, so none are declared: limits live in the
-- validation annotations.

CREATE TABLE country (
    code         TEXT  PRIMARY KEY CHECK (length(code) = 2),
    name         TEXT  NOT NULL UNIQUE,
    currency     TEXT  NOT NULL CHECK (length(currency) = 3),
    usd_per_unit FLOAT NOT NULL CHECK (usd_per_unit > 0)
);

-- Department and job title are checked in code against the fixed list
-- (docs/decisions/0001-seed-data.md), so the list lives in one place.
CREATE TABLE employee (
    id            INTEGER PRIMARY KEY,
    full_name     TEXT    NOT NULL,
    email         TEXT    NOT NULL UNIQUE COLLATE NOCASE,
    job_title     TEXT    NOT NULL,
    department    TEXT    NOT NULL,
    country_code  TEXT    NOT NULL REFERENCES country (code),
    annual_salary BIGINT  NOT NULL CHECK (annual_salary > 0),
    hire_date     DATE    NOT NULL
);

CREATE INDEX idx_employee_country    ON employee (country_code);
CREATE INDEX idx_employee_department ON employee (department);
CREATE INDEX idx_employee_job_title  ON employee (job_title);
