# Requirements — ACME Salary Management

## Goal
ACME pays 10,000 employees across several countries and manages salaries in Excel. That makes it slow to update records, easy to break data, and hard to answer simple questions like "what do we pay engineers in Spain compared to Germany?".

This tool replaces the spreadsheet with one place where the HR Manager can keep salary data correct and get answers about how the organization pays, in seconds.

## User
One persona: the **HR Manager**. They add, update and remove employees, look people up, and need numbers to back pay decisions and reviews.

## Questions the HR Manager must be able to answer
1. What is the minimum, maximum and average salary **per country**?
2. What is the minimum, maximum and average salary **for a job title within a country**?
3. Same figures **per department**.
4. How many people do we employ, and what is the total annual payroll, per country and per department?
5. How does the same job title compare **across countries**?
6. Who are the employees in a given country / department / job title, sorted by salary?

Any figure that mixes countries (Q3, Q4 per department, Q5, and Q6 without a country filter) is shown in **USD**. Figures within one country are shown in its local currency and in USD.

Every feature below exists to answer one of these. Anything that doesn't is out of scope.

## Scope (in)
- **Employee management:** create, edit and delete an employee (hard delete, confirmed in the UI). Validation: required fields, salary > 0, unique email, hire date not in the future, country / department / job title from the known lists.
- **Employee list:** search by name, filter by country, department and job title, sort by salary (in USD when no country is selected). Server-side pagination. *(Q6)*
- **Salary insights:** min / max / average / headcount / total payroll grouped by country, department, and job title within a country. Calculated in SQL, not in application code. *(Q1–Q5)*
- **Seed script:** reproducible load of 10,000 employees (fixed random seed) across at least five countries with different currencies, and the fixed lists of departments and job titles.
- **Deployed app + demo video.**

## Data model (v1)
- **Country:** code, name, currency, fixed rate to USD.
- **Employee:** id, full name, email (unique), job title, department, country, annual gross salary (local currency), hire date.

Job title and department are plain indexed text columns, validated against a fixed list defined in code. There is no screen to manage them, so separate tables would add joins without adding value. Validating them matters: free text would split "Engineer" and "engineer" into two groups, which is exactly the kind of broken data the spreadsheet produces. Country is a table because it carries the currency and the rate.

Salaries are stored as **integers in whole currency units**: annual gross pay doesn't need cents, and integer sums are exact. USD figures are rounded to whole dollars.

## Assumptions
- Salaries are **annual gross**, stored in the **country's local currency**.
- Cross-country figures are converted to one **reporting currency (USD)** using a **fixed rate per country**, stored in the database. Rates are set once and not updated live.
- Conversion is applied **per employee, before aggregating**. Averaging local amounts first and converting afterwards gives wrong results as soon as a group spans more than one country.
- A single HR Manager uses the tool; there is no concurrent editing conflict to solve.
- Stack: Java + Spring Boot, SQLite, Angular.

## Out of scope (and why)
- **Login and roles:** the brief has one persona. Auth adds work that answers none of the questions above. It would be the first thing to add before real use.
- **Payroll, taxes and deductions:** rules differ per country and are a different product. We work with gross salary only.
- **Salary history / audit log:** useful for real HR, but it changes the data model (history tables). v1 overwrites the current salary, and a deleted employee is gone.
- **Live exchange rates:** depends on an external service and makes figures change by themselves. Fixed rates are enough to compare.
- **Median and percentiles:** more useful than the average for pay, but SQLite has no built-in function for them; they need window functions. First candidate for the next iteration.
- **Excel import/export:** the seed covers the data load for this exercise. A real migration off Excel would need import first.

## Non-functional
- **Performance:** list and summary endpoints answer in under 300 ms with 10,000 rows on the deployed server. Pagination, filtering and grouping run in the database, with indexes on the filtered columns (country, department, job title).
- **Tests:** written test-first. Validation is covered by plain unit tests. The summary queries live in SQL, so mocking the repository would test nothing: they are tested against a small fixed dataset in a throwaway SQLite database, where the expected figures can be worked out by hand. Fast and deterministic, never the 10,000-row seed.
- **Delivery:** incremental commits that show the test-first cycle, deployed with Docker, AI prompts kept in `docs/prompts/`.
