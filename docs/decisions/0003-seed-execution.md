# 0003 — Seed: when it runs, where names come from, and what it produced

**Date:** 2026-10-31
**Status:** accepted
**Builds on:** [0001 — Seed data](0001-seed-data.md), [0002 — Salary bands](0002-salary-bands.md)

## Decision

### When it runs

`EmployeeSeeder` runs on startup and loads `seed.employees` employees, **only into an empty database**.

| Profile | `seed.employees` |
|---|---|
| default (local, Docker) | 10000 |
| test | 0: tests build their own small datasets |

Seeding only an empty table makes restarts safe. Without that check, a restart would try to insert the same employees again and the unique email constraint would stop the application from starting; a test reproduces exactly that.

### Reproducibility

- One `Random` with a fixed seed (42) drives everything: country, department, job title, position in the pay band, hire date and names.
- Weights are kept in insertion order (`WeightedChoice` takes a `SequencedMap`). `Map.of` changes its iteration order between JVM runs, and the same seed would then produce different employees.
- Name generators are created on first use, in generation order, for the same reason.
- Hire dates run from 2012-01-01 to 2026-10-23, the date of the exchange rates, used as the dataset's "as of" date. A fixed date instead of `LocalDate.now()` keeps the seed the same every day and never puts a hire date in the future.

### Names

- DataFaker 2.7.0, in each country's language: `en-US`, `en-GB`, `de`, `es`, `es-MX`, `en-IN`.
- **China:** DataFaker only has Chinese names in Chinese characters. The seed uses the 20 most common family names and 20 common given names in pinyin, family name first as they are written in China (`Wang Wei`), so every name in the list can be read and searched with a Latin keyboard.
- Emails come from the employee number (`employee42@acme.example`), not from the name: unique by construction, and no transliteration needed.

## Actual result

The Java seed (seed 42, 10,000 employees), queried with SQL after loading:

| Country | Employees |
|---|---|
| US | 2,480 |
| IN | 2,080 |
| GB | 1,209 |
| DE | 1,172 |
| ES | 1,142 |
| MX | 1,049 |
| CN | 868 |

| Department | Employees |
|---|---|
| Engineering | 3,751 |
| Sales | 2,228 |
| Operations | 1,767 |
| Marketing | 934 |
| Finance | 797 |
| Human Resources | 523 |

- **Managers:** 1,228 (12.3%).
- **Software Engineers, Spain vs Germany:** 283 in Spain, 34,000 to 50,000 EUR (avg 42,150); 233 in Germany, 57,700 to 84,900 EUR (avg 71,566).
- **Average salary in USD:** US 104,545 · GB 74,832 · DE 70,283 · ES 41,544 · CN 39,522 · MX 31,128 · IN 26,971.
- **Hire dates:** 2012-01-01 to 2026-10-22, stored as text.
- **Load time:** about 8 seconds on a development laptop. A restart with data skips the seed.

The figures differ slightly from the Python prototype in 0001 and 0002, as expected: the random generator is different. They stay the same from one run to the next.
