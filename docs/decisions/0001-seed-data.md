# 0001 — Seed data: countries, rates, departments, job titles and weights

**Date:** 2026-10-31
**Status:** accepted

## Context

The seed loads 10,000 employees. If countries, departments and job titles were picked uniformly, half the company would be managers, and every summary would describe an organization that doesn't exist. The data has to look like a real multinational so that the figures the HR Manager sees are believable.

## Decision

### Countries and exchange rates

| Code | Country | Currency | USD per unit |
|---|---|---|---|
| US | United States | USD | 1 |
| ES | Spain | EUR | 1.1199 |
| DE | Germany | EUR | 1.1199 |
| GB | United Kingdom | GBP | 1.3375 |
| CN | China | CNY | 0.149921 |
| IN | India | INR | 0.010533 |
| MX | Mexico | MXN | 0.053580 |

- Codes are ISO 3166-1 alpha-2. The United Kingdom is `GB`, not `UK`.
- **Source:** Federal Reserve, H.10 release of 2026-10-26, rates as of 2026-10-23 (https://www.federalreserve.gov/releases/h10/).
- The Fed quotes EUR and GBP as USD per unit, and CNY, INR and MXN as units per USD. The last three are stored as the inverse (1/6.6702, 1/94.94, 1/18.6638), so converting is always a multiplication: `salary * usd_per_unit`.
- Six decimals are enough: the worst case (INR) is off by less than 1 USD on a 3,000,000 INR salary, below the whole-dollar rounding.
- Spain and Germany share a currency on purpose: it proves that grouping is by country, not by currency.

### Departments and job titles

A job title belongs to one department. The mapping is fixed in code, and validation rejects a title that doesn't belong to the employee's department.

| Department | Job titles |
|---|---|
| Engineering | Software Engineer, Senior Software Engineer, Engineering Manager |
| Sales | Account Executive, Sales Manager |
| Operations | Operations Analyst, Operations Manager |
| Marketing | Marketing Specialist, Marketing Manager |
| Finance | Financial Analyst, Finance Manager |
| Human Resources | HR Specialist, HR Manager |

### Weights

Each employee is drawn in three independent steps: country, then department, then a job title within that department.

| Country | Weight |
|---|---|
| US | 25 |
| IN | 20 |
| GB | 12 |
| DE | 12 |
| ES | 12 |
| MX | 10 |
| CN | 9 |

| Department | Weight |
|---|---|
| Engineering | 38 |
| Sales | 22 |
| Operations | 18 |
| Marketing | 9 |
| Finance | 8 |
| Human Resources | 5 |

| Department | Job title weights |
|---|---|
| Engineering | Software Engineer 62 · Senior Software Engineer 28 · Engineering Manager 10 |
| Sales | Account Executive 88 · Sales Manager 12 |
| Operations | Operations Analyst 88 · Operations Manager 12 |
| Marketing | Marketing Specialist 85 · Marketing Manager 15 |
| Finance | Financial Analyst 85 · Finance Manager 15 |
| Human Resources | HR Specialist 85 · HR Manager 15 |

Smaller departments get a slightly higher share of managers: a team of 40 still needs a manager.

## Expected result

A prototype of the draw (Python, seed 42, 10,000 employees) gave:

| Country | Employees | Expected |
|---|---|---|
| US | 2,449 | 2,500 |
| IN | 2,031 | 2,000 |
| ES | 1,223 | 1,200 |
| DE | 1,204 | 1,200 |
| GB | 1,163 | 1,200 |
| MX | 1,018 | 1,000 |
| CN | 912 | 900 |

| Department | Employees | Expected |
|---|---|---|
| Engineering | 3,841 | 3,800 |
| Sales | 2,230 | 2,200 |
| Operations | 1,755 | 1,800 |
| Marketing | 929 | 900 |
| Finance | 769 | 800 |
| Human Resources | 476 | 500 |

| Job title | Employees | Expected |
|---|---|---|
| Software Engineer | 2,338 | 2,356 |
| Account Executive | 1,951 | 1,936 |
| Operations Analyst | 1,556 | 1,584 |
| Senior Software Engineer | 1,126 | 1,064 |
| Marketing Specialist | 775 | 765 |
| Financial Analyst | 670 | 680 |
| HR Specialist | 401 | 425 |
| Engineering Manager | 377 | 380 |
| Sales Manager | 279 | 264 |
| Operations Manager | 199 | 216 |
| Marketing Manager | 154 | 135 |
| Finance Manager | 99 | 120 |
| HR Manager | 75 | 75 |

**Managers: 1,183 of 10,000 (11.8%), one per 7.5 employees.**

The Java seed uses its own random generator, so its exact counts will differ from these, but with a fixed seed they are always the same from one run to the next.

## Consequences

- **Counts are close to the weights, not exact.** That is the cost of a weighted random draw; a fixed seed makes it reproducible, which is what the requirements ask for.
- **Every country + job title pair has people** (91 of 91), so no Q2 query comes back empty. The smallest cell is HR Manager in China, with 4 people: min, max and average there come from four salaries. That is realistic for a small subsidiary, and worth saying out loud if it shows up in the demo.
- **Country and department are drawn independently**, so India has the same 38% of engineers as Spain. A real company would vary by country, but this way cross-country comparisons differ only in salaries, which is what Q5 is about.
- **Salaries are not decided here.** Pay bands per job title and country are the next decision.
