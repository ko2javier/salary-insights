# 0004 — Performance: what makes it fast enough, and the measurements

**Date:** 2026-11-07
**Status:** accepted
**Requirement:** [REQUIREMENTS.md](../../REQUIREMENTS.md), *Non-functional*: list and summary endpoints under 300 ms with 10,000 rows on the deployed server.

The brief asks for performance considerations but gives no number. **300 ms is our own target**: below that, a click on a filter feels immediate.

## Decision

1. **Every list and every figure is computed by SQLite, never in Java.** The API returns one page of employees or one row per group: at most 100 employees (`size` is capped at 1–100) or 13 rows (the job titles), never the 10,000.
2. **Indexes on the three filter columns:** `country_code`, `department` and `job_title` (`V1__create_schema.sql`). Country codes are the primary key of `country`.
3. **No index for the salary order.** The list is sorted by salary in USD, `annual_salary × usd_per_unit`, and the rate lives in another table. SQLite cannot index an expression that spans two tables, so it sorts the filtered rows in a temporary B-tree. With 10,000 rows that costs a few milliseconds (measured below). The alternative, a stored `salary_usd` column, would need recalculating whenever a rate changes; it is not worth it at this size.
4. **The browser asks once for what never changes.** The catalog (countries, departments, job titles) is requested once per session (`shareReplay`). Angular's build files carry a hash in their name and are cached for a year; `index.html` is never cached, so a new version shows up on the next visit.

## Query plans

`EXPLAIN QUERY PLAN` on the seeded database (10,000 employees):

| Query | Plan |
|---|---|
| List filtered by country and department | `SEARCH e USING INDEX idx_employee_department`, then a temporary B-tree for the order |
| List filtered by job title | `SEARCH e USING INDEX idx_employee_job_title`, then a temporary B-tree for the order |
| List without filters, sorted by USD | `SCAN e` (all rows), temporary B-tree for the order |
| Search by name (`LIKE '%an%'`) | `SCAN e`: a pattern that starts with `%` cannot use an index |
| Figures per country | `SCAN e USING INDEX idx_employee_country`, temporary B-tree for the `GROUP BY` |
| Job titles within a country | `SEARCH e USING INDEX idx_employee_country (country_code=?)` |

## Measurements on the deployed server

Hetzner VPS (4 vCPU, 8 GB RAM, shared with other services), 2026-11-07. Docker Compose as in this repository: backend limited to 512 MB with a 384 MB heap. Each request made 20 times from the server itself through the frontend's nginx (`http://127.0.0.1:8095/api/...`), so the time includes nginx, Spring, SQLite and the JSON, but not the internet.

| Endpoint | Median | p95 | Max |
|---|---|---|---|
| `GET /api/employees` (first page, by USD) | 27 ms | 36 ms | 101 ms * |
| `GET /api/employees?sort=asc&page=499` (last page) | 56 ms | 67 ms | 74 ms |
| `GET /api/employees?country=DE&department=Engineering&sort=asc` | 16 ms | 22 ms | 24 ms |
| `GET /api/employees?search=an` | 29 ms | 34 ms | 35 ms |
| `GET /api/employees?jobTitle=HR Manager` | 14 ms | 16 ms | 18 ms |
| `GET /api/insights/summary` | 18 ms | 20 ms | 20 ms |
| `GET /api/insights/countries` | 35 ms | 39 ms | 49 ms |
| `GET /api/insights/countries?jobTitle=HR Manager` | 11 ms | 14 ms | 18 ms |
| `GET /api/insights/countries/US/job-titles` | 12 ms | 15 ms | 16 ms |
| `GET /api/insights/departments` | 22 ms | 26 ms | 27 ms |
| `GET /api/catalog` | 8 ms | 9 ms | 31 ms |

\* The first request after the deployment, while the JVM was still warming up.

**Every endpoint stays far below 300 ms**: the slowest median is 56 ms, the last page, where SQLite has to sort all 10,000 rows and skip 9,980 of them.

From a browser in Spain, through Cloudflare and the reverse proxy, the same list takes about 225 ms (median of five). Almost all of it is the network.

## When this stops being enough

These choices are right for 10,000 employees. If the data grew a hundredfold:

- **Name search** would be the first to suffer, because it reads every row. Fix: SQLite's FTS5 full-text index, or a normalized name column with a prefix search.
- **Deep pages** sort the whole result to skip most of it. Fix: keyset pagination (continue after the last salary seen instead of `OFFSET`).
- **The USD sort** would justify a stored `salary_usd` column with its own index.
