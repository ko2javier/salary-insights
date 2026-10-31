# 0002 — Seed data: salary bands

**Date:** 2026-10-31
**Status:** accepted
**Builds on:** [0001 — Seed data](0001-seed-data.md)

## Context

Every seeded employee needs an annual gross salary in their country's local currency. With 13 job titles and 7 countries there are 91 combinations: writing a band for each by hand would be 182 numbers that nobody could justify one by one.

## Decision

Salaries come from **13 bands (one per job title), 7 pay factors (one per country) and 6 department adjustments**: 26 numbers in total.

```
salary_local = uniform(band_min, band_max)
             × pay_factor[country]
             × adjustment[department, country]   (1.0 unless listed below)
             ÷ usd_per_unit[country]
```

rounded to the nearest 100 units of local currency.

### Bands per job title (USD, United States as reference, annual gross)

| Job title | Min | Max |
|---|---|---|
| Software Engineer | 95,000 | 140,000 |
| Senior Software Engineer | 135,000 | 190,000 |
| Engineering Manager | 170,000 | 240,000 |
| Account Executive | 60,000 | 110,000 |
| Sales Manager | 110,000 | 170,000 |
| Operations Analyst | 55,000 | 85,000 |
| Operations Manager | 95,000 | 145,000 |
| Marketing Specialist | 55,000 | 85,000 |
| Marketing Manager | 100,000 | 155,000 |
| Financial Analyst | 65,000 | 95,000 |
| Finance Manager | 110,000 | 165,000 |
| HR Specialist | 55,000 | 80,000 |
| HR Manager | 95,000 | 140,000 |

### Pay factor per country (relative to the United States)

| Country | Factor |
|---|---|
| US | 1.00 |
| GB | 0.70 |
| DE | 0.68 |
| ES | 0.40 |
| CN | 0.32 |
| MX | 0.28 |
| IN | 0.23 |

### Department adjustments (exceptions; every other pair is 1.0)

| Department × Country | Adjustment | Why |
|---|---|---|
| Engineering × IN | 1.30 | Multinationals compete for engineers in their tech centres |
| Engineering × CN | 1.30 | Same |
| Engineering × MX | 1.15 | Nearshoring: engineering teams working for the US |
| Finance × GB | 1.15 | London as a financial centre |
| Operations × IN | 0.85 | Large local supply for back-office roles |
| Human Resources × IN | 0.85 | Same |

Without these, one factor per country would make every job title keep the same ratio to the US, and Q5 would always show the same picture. With them, Q5 shows that how a country pays depends on the role.

**These figures are order-of-magnitude estimates for a synthetic dataset, not sourced market data.** What matters for the exercise is that the shape is believable (managers above their teams, Germany above Spain, the US on top) and that the queries answer correctly, not that the numbers match a salary survey.

## Expected result

Prototype of the draw (Python, seed 42, 10,000 employees, weights from 0001):

| Country | Avg local | Avg USD | Payroll USD |
|---|---|---|---|
| US | 103,740 USD | 103,740 | 257.6 M |
| GB | 55,508 GBP | 74,241 | 86.6 M |
| DE | 63,785 EUR | 71,433 | 83.9 M |
| ES | 36,927 EUR | 41,355 | 50.4 M |
| CN | 255,247 CNY | 38,267 | 34.9 M |
| MX | 588,206 MXN | 31,516 | 31.2 M |
| IN | 2,536,701 INR | 26,719 | 55.0 M |

Q5, the same job title across countries, as a percentage of the US average:

| Job title | GB | DE | ES | CN | MX | IN |
|---|---|---|---|---|---|---|
| Software Engineer | 70% | 68% | 40% | **41%** | **32%** | **30%** |
| Account Executive | 71% | 71% | 41% | 32% | 28% | 23% |
| Financial Analyst | **79%** | 67% | 39% | 31% | 27% | 23% |
| Operations Analyst | 70% | 69% | 40% | 32% | 28% | **20%** |
| HR Manager | 71% | 68% | 38% | 33% | 27% | **20%** |

In bold, the cells the adjustments move. Software Engineers in China end up slightly above Spain (48,710 vs 47,010 USD), while every other role in China stays well below.

The question in the requirements' goal, engineers in Spain vs Germany (Software Engineer, EUR):

| Country | Employees | Min | Max | Avg |
|---|---|---|---|---|
| ES | 284 | 34,000 | 49,900 | 41,977 |
| DE | 267 | 57,800 | 84,700 | 71,131 |

Total payroll 599.5 M USD. Lowest salary in the whole dataset 10,778 USD, highest 239,000 USD.

As in 0001, the Java seed uses a different random generator: its exact figures will differ from these but stay fixed from run to run.

## Consequences

- **Six adjustments, not a full department × country matrix.** A full matrix would be 42 numbers with no data behind them. Six exceptions, each with a one-line reason, are enough for Q5 to show that pay ratios differ by role, and few enough to defend one by one.
- **Adjustments apply to a whole department.** An Engineering Manager in India gets the same 1.30 as a Software Engineer; real markets would differ by seniority too.
- **Uniform draw inside the band.** The average of any large group sits near the middle of its band, and min/max near its edges. Real pay is skewed (more people near the bottom of a band); a skewed draw is a one-line change if needed.
- **Base salary only.** Account Executives get no commission and nobody gets a bonus: the requirements work with annual gross base pay.
- **Rounding to 100 local units** makes salaries look like real offers. For INR, 100 units is about 1 USD, so the rounding never distorts a figure.
