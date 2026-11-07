# Salary Insights

A salary management tool for ACME's HR Manager: 10,000 employees in 7 countries, each paid in its own currency. It replaces a spreadsheet with one place to keep salary data correct and to answer, in a couple of clicks, questions like *"what do we pay engineers in Spain compared to Germany?"*

**Live:** [salary-insights.ko2-oreilly.com](https://salary-insights.ko2-oreilly.com). It sits behind basic authentication; the credentials are shared with the reviewers separately.

[![CI](https://github.com/ko2javier/salary-insights/actions/workflows/ci.yml/badge.svg)](https://github.com/ko2javier/salary-insights/actions/workflows/ci.yml)

## Run it

Only Docker is needed: no Java, no Node, no secrets.

```bash
git clone https://github.com/ko2javier/salary-insights.git
cd salary-insights
docker compose up --build
```

Open **http://localhost:8095**. The first start loads 10,000 reproducible employees (fixed random seed) before the app answers; restarts keep the data.

## What it answers

Everything in the app exists to answer one of these questions ([REQUIREMENTS.md](REQUIREMENTS.md)).

| | Question | In the app | API |
|---|---|---|---|
| Q1 | Min, max and average salary per country | Insights → *By country* | `GET /api/insights/countries` |
| Q2 | The same for a job title within a country | Insights → *Job titles in a country*; and the employee panel, which places each person between the lowest and the highest of their job title in their country | `GET /api/insights/countries/{code}/job-titles` |
| Q3 | The same per department | Insights → *By department* | `GET /api/insights/departments` |
| Q4 | Headcount and total payroll per country and per department | Both tables above, plus the company KPIs | `GET /api/insights/summary` |
| Q5 | One job title compared across countries | Insights → *By country*, choosing a job title | `GET /api/insights/countries?jobTitle=...` |
| Q6 | Who works in a country / department / job title, sorted by salary | Employees: search, filters, sort and pages | `GET /api/employees?search&country&department&jobTitle&sort&page&size` |

Employees are created, edited and removed from the same screen: `POST`, `PUT` and `DELETE /api/employees/{id}`. Validation errors come back as RFC 9457 problem details with one message per field, shown next to each field in the form.

**Currencies.** Salaries are stored in each country's currency, as whole units. Anything that mixes countries is shown in USD, converted employee by employee *before* aggregating (averaging first and converting later gives wrong results). Inside one country the app shows USD with the local amount underneath. Rates are fixed and stored in the database.

## Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4, Spring Data JPA for the employees, `JdbcClient` and plain SQL for the figures, Flyway |
| Database | SQLite, in a Docker volume |
| Frontend | Angular 22 (standalone components, signals, zoneless), reactive forms |
| Tests | JUnit 5, AssertJ and MockMvc; Vitest for Angular |
| Delivery | Docker Compose (nginx serves Angular and forwards `/api/`), GitHub Actions CI |

## Develop without Docker

Requires Java 21 and Node 22.

```bash
# Backend on http://localhost:8080 (Swagger UI at /swagger-ui.html)
cd backend
./gradlew bootRun

# Frontend on http://localhost:4200, calling the backend above
cd frontend
npm ci
npx ng serve
```

In development the browser calls the backend on another port, so the backend allows the origin in `cors.allowed-origins` (`http://localhost:4200` by default). In Docker both share one origin and CORS plays no part.

## Tests

```bash
cd backend && ./gradlew test                 # 109 tests
cd frontend && npx ng test --watch=false     # 55 tests
```

Written test-first: the history shows each feature as a `test:` commit in red followed by a `feat:` commit in green. The figures are computed in SQL, so mocking the repository would test nothing: they are tested against a small dataset in a throwaway SQLite database, where the expected numbers can be worked out by hand. The 10,000-row seed is never used in tests.

GitHub Actions runs the backend tests, the frontend tests and the production build on every push.

## Performance

Measured on the deployed server with the 10,000 employees: every list and summary endpoint answers with a median between 8 and 56 ms, against a target of 300 ms. Filtering, sorting, pagination and grouping all happen in SQLite, with indexes on the filtered columns. Details, query plans and where it would stop scaling: [0004 — Performance](docs/decisions/0004-performance.md).

## Repository

```
backend/       Spring Boot API, Flyway migrations, seed
frontend/      Angular app, nginx.conf, Dockerfile
docs/
  decisions/   why the data, the salary bands, the seed and the performance work the way they do
  prompts/     the prompts given to the AI assistant, verbatim, with an English translation
  deployment.md  running it on a VPS behind a reverse proxy with TLS and basic auth
REQUIREMENTS.md  goal, the six questions, scope, assumptions, what is out of scope and why
```

## Decisions and limits

- [REQUIREMENTS.md](REQUIREMENTS.md): scope, assumptions, and what was left out on purpose (login, salary history, live exchange rates, medians, accent-insensitive search), each with the reason.
- [docs/decisions/](docs/decisions/): seed data, salary bands, seed execution, performance.
- [docs/deployment.md](docs/deployment.md): deployment, backups, CI and possible improvements, such as continuous deployment.

## Use of AI

This project was built pairing with an AI assistant. The prompts that shaped it are kept verbatim in [docs/prompts/](docs/prompts/), in the order of the work, with an English translation. Personal questions and side topics unrelated to the project (my own server's certificates, for example) are left out.
